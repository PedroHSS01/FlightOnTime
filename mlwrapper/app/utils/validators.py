"""
Utility validators for flight data
Currently using Pydantic for validation in routes
This module can be extended for additional custom validation logic
"""


def validate_airport_code(code: str) -> bool:
    """
    Validate airport code format (IATA 3-letter code)

    Args:
        code: Airport code to validate

    Returns:
        True if valid, False otherwise
    """
    if not code or len(code) != 3:
        return False
    return code.isalpha() and code.isupper()


def validate_airline_code(code: str) -> bool:
    """
    Validate airline code format (IATA 2-letter code)

    Args:
        code: Airline code to validate

    Returns:
        True if valid, False otherwise
    """
    if not code or len(code) != 2:
        return False
    return code.isalpha() and code.isupper()


def validate_flight_number(number: str) -> bool:
    """
    Validate flight number format

    Args:
        number: Flight number to validate

    Returns:
        True if valid, False otherwise
    """
    if not number or len(number) < 2 or len(number) > 10:
        return False
    return True


def validate_flight_distance(distance: int) -> bool:
    """
    Validate flight distance

    Args:
        distance: Flight distance in miles/km

    Returns:
        True if valid, False otherwise
    """
    return distance > 0
