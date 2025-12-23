"""
Request Logging Middleware

Adds correlation ID to each request for distributed tracing.
Logs request/response for observability.

Clean Code Principles:
- Single Responsibility: Only handles logging
- Open/Closed: Easy to extend with more logging features
"""

import logging
import uuid
import time
from flask import request, g
from functools import wraps

logger = logging.getLogger(__name__)


def add_correlation_id():
    """
    Generate or extract correlation ID for request tracing

    This helps trace requests across multiple services:
    Java API -> Flask Wrapper -> ML Service
    """
    correlation_id = request.headers.get('X-Correlation-ID')

    if not correlation_id:
        correlation_id = str(uuid.uuid4())

    g.correlation_id = correlation_id
    return correlation_id


def log_request():
    """Log incoming request with correlation ID"""
    correlation_id = add_correlation_id()

    logger.info(
        f"[{correlation_id}] Incoming {request.method} {request.path} "
        f"from {request.remote_addr}"
    )

    # Track request start time
    g.start_time = time.time()


def log_response(response):
    """Log response with correlation ID and duration"""

    if hasattr(g, 'correlation_id') and hasattr(g, 'start_time'):
        duration = time.time() - g.start_time

        logger.info(
            f"[{g.correlation_id}] Response {response.status_code} "
            f"in {duration:.3f}s"
        )

        # Add correlation ID to response headers for client
        response.headers['X-Correlation-ID'] = g.correlation_id

    return response


def log_endpoint(f):
    """
    Decorator to log endpoint execution

    Usage:
        @bp.route('/predict', methods=['POST'])
        @log_endpoint
        def predict():
            ...
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        correlation_id = getattr(g, 'correlation_id', 'unknown')

        logger.info(f"[{correlation_id}] Executing {f.__name__}")

        try:
            result = f(*args, **kwargs)
            return result
        except Exception as e:
            logger.error(
                f"[{correlation_id}] Error in {f.__name__}: {str(e)}",
                exc_info=True
            )
            raise

    return decorated_function
