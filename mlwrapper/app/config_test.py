"""
Test Configuration

Separate configuration for testing environment.
Follows Single Responsibility Principle.
"""


class TestConfig:
    """Configuration for testing environment"""

    # Flask
    TESTING = True
    DEBUG = False
    PORT = 5000

    # Mock ML Service
    ML_SERVICE_URL = "http://mock-ml-service:8000/predict"
    ML_SERVICE_TIMEOUT = 5

    # Logging
    LOG_LEVEL = "DEBUG"

    @staticmethod
    def init_app(app):
        """Initialize app with test configuration"""
        app.config['TESTING'] = True
        app.config['DEBUG'] = False
