"""
Unit tests for validators module.

Boas práticas aplicadas:
- AAA pattern (Arrange, Act, Assert)
- Testes parametrizados com pytest.mark.parametrize
- Nomenclatura descritiva (test_<function>_<scenario>_<expected_result>)
- Cobertura de casos válidos, inválidos e edge cases
- Testes isolados sem dependências externas
- Docstrings explicativas

@author FlightOnTime Team
@version 1.0
"""

import pytest
from app.utils.validators import (
    validate_airport_code,
    validate_airline_code,
    validate_flight_number,
    validate_flight_distance
)


class TestValidateAirportCode:
    """Unit tests for validate_airport_code function."""

    @pytest.mark.parametrize("valid_code", [
        "GIG",  # Rio de Janeiro
        "GRU",  # São Paulo Guarulhos
        "JFK",  # New York JFK
        "LAX",  # Los Angeles
        "LHR",  # London Heathrow
        "CDG",  # Paris Charles de Gaulle
    ])
    def test_validate_airport_code_valid_codes_returns_true(self, valid_code: str):
        """
        Given: A valid IATA 3-letter uppercase airport code
        When: validate_airport_code is called
        Then: Should return True
        """
        # Arrange - code provided via parametrize
        
        # Act
        result = validate_airport_code(valid_code)
        
        # Assert
        assert result is True, f"Valid airport code '{valid_code}' should return True"

    @pytest.mark.parametrize("invalid_code,reason", [
        ("", "empty string"),
        ("AB", "only 2 characters"),
        ("ABCD", "4 characters instead of 3"),
        ("123", "numeric characters"),
        ("gig", "lowercase letters"),
        ("Gig", "mixed case"),
        ("G1G", "contains number"),
        (None, "None value"),
        ("   ", "whitespace only"),
    ])
    def test_validate_airport_code_invalid_codes_returns_false(
        self, invalid_code: str, reason: str
    ):
        """
        Given: An invalid airport code
        When: validate_airport_code is called
        Then: Should return False
        """
        # Arrange - code provided via parametrize
        
        # Act
        result = validate_airport_code(invalid_code) if invalid_code is not None else validate_airport_code(None)
        
        # Assert
        assert result is False, f"Invalid airport code ({reason}) should return False"


class TestValidateAirlineCode:
    """Unit tests for validate_airline_code function."""

    @pytest.mark.parametrize("valid_code", [
        "AZ",  # Azul
        "LA",  # LATAM
        "AA",  # American Airlines
        "UA",  # United Airlines
        "BA",  # British Airways
        "AF",  # Air France
    ])
    def test_validate_airline_code_valid_codes_returns_true(self, valid_code: str):
        """
        Given: A valid IATA 2-letter uppercase airline code
        When: validate_airline_code is called
        Then: Should return True
        """
        # Act
        result = validate_airline_code(valid_code)
        
        # Assert
        assert result is True, f"Valid airline code '{valid_code}' should return True"

    @pytest.mark.parametrize("invalid_code,reason", [
        ("", "empty string"),
        ("A", "only 1 character"),
        ("ABC", "3 characters instead of 2"),
        ("12", "numeric characters"),
        ("az", "lowercase letters"),
        ("Az", "mixed case"),
        ("A1", "contains number"),
        (None, "None value"),
    ])
    def test_validate_airline_code_invalid_codes_returns_false(
        self, invalid_code: str, reason: str
    ):
        """
        Given: An invalid airline code
        When: validate_airline_code is called
        Then: Should return False
        """
        # Act
        result = validate_airline_code(invalid_code) if invalid_code is not None else validate_airline_code(None)
        
        # Assert
        assert result is False, f"Invalid airline code ({reason}) should return False"


class TestValidateFlightNumber:
    """Unit tests for validate_flight_number function."""

    @pytest.mark.parametrize("valid_number", [
        "AZ1234",
        "AA100",
        "UA999",
        "BA12345678",  # 10 chars (max)
        "G3",          # 2 chars (min)
    ])
    def test_validate_flight_number_valid_numbers_returns_true(self, valid_number: str):
        """
        Given: A valid flight number (2-10 characters)
        When: validate_flight_number is called
        Then: Should return True
        """
        # Act
        result = validate_flight_number(valid_number)
        
        # Assert
        assert result is True, f"Valid flight number '{valid_number}' should return True"

    @pytest.mark.parametrize("invalid_number,reason", [
        ("", "empty string"),
        ("A", "only 1 character"),
        ("A" * 11, "11 characters (exceeds max)"),
        (None, "None value"),
    ])
    def test_validate_flight_number_invalid_numbers_returns_false(
        self, invalid_number: str, reason: str
    ):
        """
        Given: An invalid flight number
        When: validate_flight_number is called
        Then: Should return False
        """
        # Act
        result = validate_flight_number(invalid_number) if invalid_number is not None else validate_flight_number(None)
        
        # Assert
        assert result is False, f"Invalid flight number ({reason}) should return False"


class TestValidateFlightDistance:
    """Unit tests for validate_flight_distance function."""

    @pytest.mark.parametrize("valid_distance", [
        1,      # minimum valid
        100,    # short flight
        500,    # medium flight
        3000,   # domestic long haul
        10000,  # international
        20000,  # ultra long haul
    ])
    def test_validate_flight_distance_valid_distances_returns_true(self, valid_distance: int):
        """
        Given: A positive flight distance
        When: validate_flight_distance is called
        Then: Should return True
        """
        # Act
        result = validate_flight_distance(valid_distance)
        
        # Assert
        assert result is True, f"Valid distance {valid_distance} should return True"

    @pytest.mark.parametrize("invalid_distance,reason", [
        (0, "zero distance"),
        (-1, "negative distance"),
        (-100, "large negative distance"),
    ])
    def test_validate_flight_distance_invalid_distances_returns_false(
        self, invalid_distance: int, reason: str
    ):
        """
        Given: An invalid flight distance (zero or negative)
        When: validate_flight_distance is called
        Then: Should return False
        """
        # Act
        result = validate_flight_distance(invalid_distance)
        
        # Assert
        assert result is False, f"Invalid distance ({reason}) should return False"


class TestValidatorEdgeCases:
    """Edge case tests for all validators."""

    def test_airport_code_with_special_characters_returns_false(self):
        """
        Given: Airport code with special characters
        When: validate_airport_code is called
        Then: Should return False
        """
        assert validate_airport_code("G@G") is False
        assert validate_airport_code("G-G") is False
        assert validate_airport_code("G.G") is False

    def test_airline_code_with_special_characters_returns_false(self):
        """
        Given: Airline code with special characters
        When: validate_airline_code is called
        Then: Should return False
        """
        assert validate_airline_code("A@") is False
        assert validate_airline_code("A-") is False

    def test_flight_number_boundary_lengths(self):
        """
        Given: Flight numbers at exact boundary lengths
        When: validate_flight_number is called
        Then: Should return correct boolean based on length
        """
        # Exactly 2 chars - valid
        assert validate_flight_number("AB") is True
        
        # Exactly 10 chars - valid
        assert validate_flight_number("A" * 10) is True
        
        # Exactly 1 char - invalid
        assert validate_flight_number("A") is False
        
        # Exactly 11 chars - invalid
        assert validate_flight_number("A" * 11) is False

    def test_flight_distance_boundary_values(self):
        """
        Given: Flight distances at boundary values
        When: validate_flight_distance is called
        Then: Should return correct boolean
        """
        # Boundary: exactly 0 - invalid
        assert validate_flight_distance(0) is False
        
        # Boundary: exactly 1 - valid
        assert validate_flight_distance(1) is True
