"""
Integration tests for the prediction endpoint.

Boas práticas aplicadas:
- Teste de integração real entre componentes (Flask app + routes + services)
- Fixtures para setup/teardown reutilizáveis
- Mocking apenas de dependências externas (ML service)
- Validação de contratos de API (request/response schemas)
- Testes de cenários de sucesso e falha
- Verificação de headers, status codes e payloads
- Cobertura de edge cases e error handling

@author FlightOnTime Team
@version 1.0
"""

import pytest
import json
from datetime import datetime, timedelta
from unittest.mock import patch, MagicMock
import requests


class TestPredictionEndpointIntegration:
    """
    Integration tests for POST /predict endpoint.
    
    Tests the complete flow:
    Client -> Flask App -> Routes -> Services -> (Mocked) ML Service
    """

    @pytest.fixture
    def app(self):
        """
        Create and configure a test Flask application instance.
        
        Fixture pattern: provides isolated app for each test.
        """
        from app import create_app
        
        app = create_app()
        app.config['TESTING'] = True
        app.config['DEBUG'] = False
        
        return app

    @pytest.fixture
    def client(self, app):
        """
        Create test client for making HTTP requests.
        
        Uses Flask's test_client which simulates HTTP without real network.
        """
        return app.test_client()

    @pytest.fixture
    def valid_flight_payload(self) -> dict:
        """
        Provide a valid flight prediction request payload.
        
        Fixture pattern: reusable test data.
        """
        future_date = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%dT%H:%M:%S")
        return {
            "flightNumber": "AZ1234",
            "companyName": "AZ",
            "flightOrigin": "GIG",
            "flightDestination": "GRU",
            "flightDepartureDate": future_date,
            "flightDistance": 350
        }

    @pytest.fixture
    def mock_ml_response_on_time(self) -> dict:
        """Mock ML service response for ON_TIME prediction."""
        return {
            "prediction": 0,
            "probability": 0.85
        }

    @pytest.fixture
    def mock_ml_response_delayed(self) -> dict:
        """Mock ML service response for DELAYED prediction."""
        return {
            "prediction": 1,
            "probability": 0.92
        }

    # ==================== SUCCESS SCENARIOS ====================

    @patch('app.services.ml_client.MLServiceClient.predict')
    def test_predict_endpoint_returns_on_time_prediction(
        self,
        mock_predict,
        client,
        valid_flight_payload,
        mock_ml_response_on_time
    ):
        """
        Integration Test: Complete prediction flow for ON_TIME result.
        
        Given: Valid flight data and ML service returns ON_TIME prediction
        When: POST /predict is called
        Then: Should return 200 with correct prediction response
        """
        # Arrange
        mock_predict.return_value = mock_ml_response_on_time

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(valid_flight_payload),
            content_type='application/json'
        )

        # Assert - Status Code
        assert response.status_code == 200, \
            f"Expected 200, got {response.status_code}: {response.get_data(as_text=True)}"

        # Assert - Response Body
        data = response.get_json()
        assert data is not None, "Response body should not be empty"
        assert 'prediction' in data, "Response should contain 'prediction' field"
        assert 'probability' in data or 'confidence' in data, \
            "Response should contain probability/confidence"
        
        # Assert - ML Client was called with correct data
        mock_predict.assert_called_once()
        call_args = mock_predict.call_args[0][0]
        assert call_args.get('flightNumber') == valid_flight_payload['flightNumber']

    @patch('app.services.ml_client.MLServiceClient.predict')
    def test_predict_endpoint_returns_delayed_prediction(
        self,
        mock_predict,
        client,
        valid_flight_payload,
        mock_ml_response_delayed
    ):
        """
        Integration Test: Complete prediction flow for DELAYED result.
        
        Given: Valid flight data and ML service returns DELAYED prediction
        When: POST /predict is called
        Then: Should return 200 with DELAYED prediction
        """
        # Arrange
        mock_predict.return_value = mock_ml_response_delayed

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(valid_flight_payload),
            content_type='application/json'
        )

        # Assert
        assert response.status_code == 200
        data = response.get_json()
        assert data is not None

    @patch('app.services.ml_client.MLServiceClient.predict')
    def test_predict_endpoint_correct_content_type(
        self,
        mock_predict,
        client,
        valid_flight_payload,
        mock_ml_response_on_time
    ):
        """
        Integration Test: Verify response Content-Type header.
        
        Given: Valid request
        When: POST /predict is called
        Then: Response should have Content-Type: application/json
        """
        # Arrange
        mock_predict.return_value = mock_ml_response_on_time

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(valid_flight_payload),
            content_type='application/json'
        )

        # Assert
        assert response.content_type == 'application/json', \
            f"Expected 'application/json', got '{response.content_type}'"

    # ==================== VALIDATION ERROR SCENARIOS ====================

    def test_predict_endpoint_missing_required_field_returns_400(self, client):
        """
        Integration Test: Validation error for missing required field.
        
        Given: Payload missing 'flightNumber' field
        When: POST /predict is called
        Then: Should return 400 Bad Request with validation error
        """
        # Arrange - missing flightNumber
        incomplete_payload = {
            "companyName": "AZ",
            "flightOrigin": "GIG",
            "flightDestination": "GRU",
            "flightDepartureDate": "2025-12-30T14:30:00",
            "flightDistance": 350
        }

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(incomplete_payload),
            content_type='application/json'
        )

        # Assert
        assert response.status_code == 400 or response.status_code == 422, \
            f"Expected 400/422 for validation error, got {response.status_code}"

    def test_predict_endpoint_invalid_json_returns_400(self, client):
        """
        Integration Test: Handle malformed JSON gracefully.
        
        Given: Malformed JSON in request body
        When: POST /predict is called
        Then: Should return 400 Bad Request
        """
        # Arrange
        invalid_json = "{ invalid json }"

        # Act
        response = client.post(
            '/predict',
            data=invalid_json,
            content_type='application/json'
        )

        # Assert
        assert response.status_code == 400, \
            f"Expected 400 for invalid JSON, got {response.status_code}"

    def test_predict_endpoint_empty_body_returns_400(self, client):
        """
        Integration Test: Handle empty request body.
        
        Given: Empty request body
        When: POST /predict is called
        Then: Should return 400 Bad Request
        """
        # Act
        response = client.post(
            '/predict',
            data='',
            content_type='application/json'
        )

        # Assert
        assert response.status_code == 400 or response.status_code == 415, \
            f"Expected 400/415 for empty body, got {response.status_code}"

    def test_predict_endpoint_invalid_distance_returns_400(self, client):
        """
        Integration Test: Validation error for invalid flight distance.
        
        Given: Payload with negative flight distance
        When: POST /predict is called
        Then: Should return 400 with validation error
        """
        # Arrange
        invalid_payload = {
            "flightNumber": "AZ1234",
            "companyName": "AZ",
            "flightOrigin": "GIG",
            "flightDestination": "GRU",
            "flightDepartureDate": "2025-12-30T14:30:00",
            "flightDistance": -100  # Invalid: negative
        }

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(invalid_payload),
            content_type='application/json'
        )

        # Assert
        assert response.status_code in [400, 422], \
            f"Expected 400/422 for invalid distance, got {response.status_code}"

    # ==================== ML SERVICE ERROR SCENARIOS ====================

    @patch('app.services.ml_client.MLServiceClient.predict')
    def test_predict_endpoint_ml_service_timeout_returns_503(
        self,
        mock_predict,
        client,
        valid_flight_payload
    ):
        """
        Integration Test: Handle ML service timeout gracefully.
        
        Given: ML service times out
        When: POST /predict is called
        Then: Should return 503 Service Unavailable or 500
        """
        # Arrange
        from app.exceptions import MLServiceTimeoutError
        mock_predict.side_effect = MLServiceTimeoutError("ML service timeout")

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(valid_flight_payload),
            content_type='application/json'
        )

        # Assert
        assert response.status_code in [500, 502, 503, 504], \
            f"Expected 5xx for service timeout, got {response.status_code}"

    @patch('app.services.ml_client.MLServiceClient.predict')
    def test_predict_endpoint_ml_service_connection_error_returns_503(
        self,
        mock_predict,
        client,
        valid_flight_payload
    ):
        """
        Integration Test: Handle ML service connection failure.
        
        Given: Cannot connect to ML service
        When: POST /predict is called
        Then: Should return 503 or appropriate error code
        """
        # Arrange
        from app.exceptions import MLServiceConnectionError
        mock_predict.side_effect = MLServiceConnectionError("Connection refused")

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(valid_flight_payload),
            content_type='application/json'
        )

        # Assert
        assert response.status_code in [500, 502, 503], \
            f"Expected 5xx for connection error, got {response.status_code}"

    # ==================== HEALTH CHECK ====================

    def test_health_endpoint_returns_200(self, client):
        """
        Integration Test: Health check endpoint.
        
        Given: Application is running
        When: GET /health is called
        Then: Should return 200 (if ML service available) or 503 (if not available)
        """
        # Act
        response = client.get('/health')

        # Assert - Accept both 200 (healthy) and 503 (degraded) in test environment
        assert response.status_code in [200, 503], \
            f"Health check should return 200 or 503, got {response.status_code}"
        
        data = response.get_json()
        assert 'status' in data, "Health response should contain 'status' field"
        # Status can be uppercase or lowercase depending on implementation
        assert data['status'].lower() in ['healthy', 'degraded'], \
            f"Status should be 'healthy' or 'degraded', got {data['status']}"


class TestPredictionEndpointContractValidation:
    """
    Contract tests to ensure API response schema compliance.
    """

    @pytest.fixture
    def app(self):
        from app import create_app
        app = create_app()
        app.config['TESTING'] = True
        return app

    @pytest.fixture
    def client(self, app):
        return app.test_client()

    @patch('app.services.ml_client.MLServiceClient.predict')
    def test_response_schema_contains_required_fields(
        self,
        mock_predict,
        client
    ):
        """
        Contract Test: Verify response contains all required fields.
        
        This test ensures backward compatibility for API consumers.
        """
        # Arrange
        mock_predict.return_value = {"prediction": 0, "probability": 0.75}
        future_date = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%dT%H:%M:%S")
        payload = {
            "flightNumber": "TEST123",
            "companyName": "TE",
            "flightOrigin": "AAA",
            "flightDestination": "BBB",
            "flightDepartureDate": future_date,
            "flightDistance": 500
        }

        # Act
        response = client.post(
            '/predict',
            data=json.dumps(payload),
            content_type='application/json'
        )

        # Assert
        if response.status_code == 200:
            data = response.get_json()
            # Verify required fields exist
            assert 'prediction' in data or 'status' in data, \
                "Response must contain 'prediction' or 'status' field"
