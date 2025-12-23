"""
ML Client Interface - Interface Segregation Principle (ISP)

This interface defines the contract for ML service communication.
Allows multiple implementations (real, mock, stub) following DIP.
"""

from abc import ABC, abstractmethod
from typing import Dict, Any


class IMLServiceClient(ABC):
    """
    Interface for ML Service Client

    Benefits:
    - Easy to mock in tests
    - Can swap implementations (real ML, stub, mock)
    - Follows Dependency Inversion Principle
    """

    @abstractmethod
    def predict(self, flight_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Sends prediction request to ML service

        Args:
            flight_data: Flight information dictionary

        Returns:
            Prediction result with format:
            {
                "prediction": 0 or 1,
                "probability": float (0.0 - 1.0)
            }

        Raises:
            Exception: If communication fails
        """
        pass

    @abstractmethod
    def health_check(self) -> Dict[str, Any]:
        """
        Checks ML service health

        Returns:
            Health status dictionary
        """
        pass
