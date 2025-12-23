from flask import Flask
from flask_cors import CORS
import logging
from app.config import Config
from app.middleware import log_request, log_response


def create_app():
    """Factory function to create Flask application"""

    app = Flask(__name__)

    # Logging configuration
    logging.basicConfig(
        level=getattr(logging, Config.LOG_LEVEL),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )

    # CORS to accept requests from Java API
    CORS(app, resources={
        r"/*": {
            "origins": ["http://localhost:8080", "http://localhost:*"],
            "methods": ["GET", "POST", "OPTIONS"],
            "allow_headers": ["Content-Type", "X-Correlation-ID"]
        }
    })

    # Register middleware for request/response logging
    app.before_request(log_request)
    app.after_request(log_response)

    logger = logging.getLogger(__name__)
    logger.info("Initializing Flask ML Wrapper...")
    logger.info(f"ML Service configured at: {Config.ML_SERVICE_URL}")

    # Register blueprints
    from app.routes import prediction_routes
    app.register_blueprint(prediction_routes.bp)

    logger.info("Flask ML Wrapper initialized successfully")

    return app
