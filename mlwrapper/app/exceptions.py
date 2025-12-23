"""
Custom Exceptions for ML Wrapper

Following Clean Code principles:
- Specific exceptions for different error scenarios
- Better error handling and logging
- Easier debugging and monitoring
"""


class MLServiceError(Exception):
    """Base exception for ML service errors"""

    def __init__(self, message: str, status_code: int = 500):
        self.message = message
        self.status_code = status_code
        super().__init__(self.message)


class MLServiceTimeoutError(MLServiceError):
    """Exception raised when ML service times out"""

    def __init__(self, message: str = "ML service did not respond in time"):
        super().__init__(message, status_code=504)


class MLServiceConnectionError(MLServiceError):
    """Exception raised when cannot connect to ML service"""

    def __init__(self, message: str = "Could not connect to ML service"):
        super().__init__(message, status_code=503)


class MLServiceHTTPError(MLServiceError):
    """Exception raised when ML service returns HTTP error"""

    def __init__(self, message: str, status_code: int):
        super().__init__(message, status_code=status_code)


class ValidationError(MLServiceError):
    """Exception raised for validation errors"""

    def __init__(self, message: str, details: dict = None):
        super().__init__(message, status_code=400)
        self.details = details
