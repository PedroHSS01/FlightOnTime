"""
Tests for ML Service Client

Tests the HTTP client layer including:
- Successful predictions
- Error handling
- Retry logic
- Performance metrics
"""

import pytest
from unittest.mock import Mock, patch
from app.services.ml_client import MLServiceClient
from app.exceptions import (
    MLServiceTimeoutError,
    MLServiceConnectionError,
    MLServiceHTTPError
)
import requests


class TestMLServiceClient:
    """Tests for MLServiceClient"""

    @pytest.fixture
    def ml_client(self):
        """Create ML client instance"""
        return MLServiceClient()

    def test_predict_success(self, ml_client):
        """Test successful prediction"""

        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
            "prediction": 1,
            "probability": 0.92
        }
        mock_response.raise_for_status = Mock()

        with patch.object(ml_client.session, 'post', return_value=mock_response):
            result = ml_client.predict({
                "flightNumber": "AA1234",
                "flightDistance": 3974
            })

            assert result['prediction'] == 1
            assert result['probability'] == 0.92

    def test_predict_timeout(self, ml_client):
        """Test prediction timeout"""

        with patch.object(ml_client.session, 'post', side_effect=requests.exceptions.Timeout):
            with pytest.raises(MLServiceTimeoutError):
                ml_client.predict({"flightNumber": "AA1234"})

    def test_predict_connection_error(self, ml_client):
        """Test connection error"""

        with patch.object(
            ml_client.session, 'post',
            side_effect=requests.exceptions.ConnectionError
        ):
            with pytest.raises(MLServiceConnectionError):
                ml_client.predict({"flightNumber": "AA1234"})

    def test_predict_http_error(self, ml_client):
        """Test HTTP error from ML service"""

        mock_response = Mock()
        mock_response.status_code = 500
        mock_response.content = b'{"error": "Internal server error"}'
        mock_response.json.return_value = {"error": "Internal server error"}

        http_error = requests.exceptions.HTTPError()
        http_error.response = mock_response

        with patch.object(ml_client.session, 'post', side_effect=http_error):
            with pytest.raises(MLServiceHTTPError):
                ml_client.predict({"flightNumber": "AA1234"})

    def test_health_check_success(self, ml_client):
        """Test successful health check"""

        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.json.return_value = {"status": "healthy"}
        mock_response.raise_for_status = Mock()

        with patch('requests.get', return_value=mock_response):
            result = ml_client.health_check()

            assert result['status'] == 'UP'
            assert result['ml_service'] == 'OK'

    def test_health_check_failure(self, ml_client):
        """Test health check when ML service is down"""

        with patch('requests.get', side_effect=requests.exceptions.ConnectionError):
            result = ml_client.health_check()

            assert result['status'] == 'DOWN'
            assert 'ml_service' in result

    def test_retry_configuration(self, ml_client):
        """Test that retry strategy is configured"""

        assert ml_client.session is not None
        adapter = ml_client.session.get_adapter('http://')
        assert adapter.max_retries.total == 3
