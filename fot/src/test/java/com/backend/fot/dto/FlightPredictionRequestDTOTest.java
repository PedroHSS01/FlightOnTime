package com.backend.fot.dto;

import com.backend.fot.constants.ValidationConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link FlightPredictionRequestDTO}.
 * <p>
 * Tests follow the AAA pattern (Arrange, Act, Assert) and cover:
 * <ul>
 *   <li>Happy path scenarios</li>
 *   <li>Edge cases</li>
 *   <li>Boundary conditions</li>
 *   <li>Negative scenarios</li>
 *   <li>Validation rules</li>
 *   <li>Business logic</li>
 * </ul>
 * </p>
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-15
 */
@DisplayName("FlightPredictionRequestDTO Tests")
class FlightPredictionRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== HAPPY PATH TESTS ====================

    @Nested
    @DisplayName("Happy Path Scenarios")
    class HappyPathTests {

        @Test
        @DisplayName("Should create valid DTO with all required fields")
        void shouldCreateValidDTO() {
            // Arrange & Act
            FlightPredictionRequestDTO dto = createValidDTO();

            // Assert
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should build DTO using builder pattern")
        void shouldBuildDTOUsingBuilder() {
            // Arrange & Act
            FlightPredictionRequestDTO dto = FlightPredictionRequestDTO.builder()
                    .flightNumber("AA1234")
                    .companyName("AA")
                    .flightOrigin("JFK")
                    .flightDestination("LAX")
                    .flightDepartureDate(LocalDateTime.now().plusDays(7))
                    .flightDistance(3974)
                    .build();

            // Assert
            assertAll(
                    () -> assertThat(dto.getFlightNumber()).isEqualTo("AA1234"),
                    () -> assertThat(dto.getCompanyName()).isEqualTo("AA"),
                    () -> assertThat(dto.getFlightOrigin()).isEqualTo("JFK"),
                    () -> assertThat(dto.getFlightDestination()).isEqualTo("LAX"),
                    () -> assertThat(dto.getFlightDistance()).isEqualTo(3974)
            );
        }

        @Test
        @DisplayName("Should validate short flight number (3 chars)")
        void shouldValidateShortFlightNumber() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightNumber("AA1")
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should validate long flight number (7 chars)")
        void shouldValidateLongFlightNumber() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightNumber("AAA9999")
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    // ==================== FLIGHT NUMBER VALIDATION TESTS ====================

    @Nested
    @DisplayName("Flight Number Validation")
    class FlightNumberValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("Should reject null, empty or blank flight number")
        void shouldRejectInvalidFlightNumber(String flightNumber) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightNumber(flightNumber)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting("message")
                    .contains(ValidationConstants.FLIGHT_NUMBER_REQUIRED_MSG);
        }

        @ParameterizedTest
        @ValueSource(strings = {"AA", "A123", "1234", "AAAA1234", "aa1234", "AA12345", "A-123"})
        @DisplayName("Should reject invalid flight number format")
        void shouldRejectInvalidFlightNumberFormat(String flightNumber) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightNumber(flightNumber)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting("message")
                    .contains(ValidationConstants.FLIGHT_NUMBER_INVALID_MSG);
        }

        @ParameterizedTest
        @ValueSource(strings = {"AA1", "BA99", "LH456", "AZ1234", "TAP999", "AAA9999"})
        @DisplayName("Should accept valid flight numbers")
        void shouldAcceptValidFlightNumbers(String flightNumber) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightNumber(flightNumber)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    // ==================== AIRLINE CODE VALIDATION TESTS ====================

    @Nested
    @DisplayName("Airline Code Validation")
    class AirlineCodeValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should reject null or empty airline code")
        void shouldRejectNullOrEmptyAirlineCode(String companyName) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .companyName(companyName)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"A", "AAAA", "a1", "1A", "A-B"})
        @DisplayName("Should reject invalid airline code format")
        void shouldRejectInvalidAirlineCodeFormat(String companyName) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .companyName(companyName)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"AA", "BA", "AZ", "TAP", "LH"})
        @DisplayName("Should accept valid airline codes")
        void shouldAcceptValidAirlineCodes(String companyName) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .companyName(companyName)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    // ==================== AIRPORT CODE VALIDATION TESTS ====================

    @Nested
    @DisplayName("Airport Code Validation")
    class AirportCodeValidationTests {

        @Test
        @DisplayName("Should reject null origin airport code")
        void shouldRejectNullOrigin() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightOrigin(null)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"JF", "JFKK", "jfk", "JF1", "J-K"})
        @DisplayName("Should reject invalid origin airport code format")
        void shouldRejectInvalidOriginFormat(String origin) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightOrigin(origin)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should reject same origin and destination")
        void shouldRejectSameOriginAndDestination() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightOrigin("JFK")
                    .flightDestination("JFK")
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting("message")
                    .contains(ValidationConstants.SAME_ORIGIN_DESTINATION_MSG);
        }
    }

    // ==================== DATE VALIDATION TESTS ====================

    @Nested
    @DisplayName("Departure Date Validation")
    class DepartureDateValidationTests {

        @Test
        @DisplayName("Should reject null departure date")
        void shouldRejectNullDepartureDate() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDepartureDate(null)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should reject past departure date")
        void shouldRejectPastDepartureDate() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDepartureDate(LocalDateTime.now().minusDays(1))
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting("message")
                    .contains(ValidationConstants.DEPARTURE_DATE_FUTURE_MSG);
        }

        @Test
        @DisplayName("Should accept present departure date")
        void shouldAcceptPresentDepartureDate() {
            // Arrange
            // Use plusSeconds(1) to avoid timing issues with @FutureOrPresent validation
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDepartureDate(LocalDateTime.now().plusSeconds(1))
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should reject departure date more than 365 days in future")
        void shouldRejectTooFarFutureDepartureDate() {
            // Arrange
            // Use 370 days to ensure we're clearly over the 365 day limit
            // (ChronoUnit.DAYS.between truncates, so 366 days minus a few hours might be 365)
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDepartureDate(LocalDateTime.now().plusDays(370))
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting("message")
                    .anyMatch(msg -> msg.toString().contains("365 days"));
        }
    }

    // ==================== DISTANCE VALIDATION TESTS ====================

    @Nested
    @DisplayName("Flight Distance Validation")
    class FlightDistanceValidationTests {

        @Test
        @DisplayName("Should reject null distance")
        void shouldRejectNullDistance() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(null)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("Should reject zero or negative distance")
        void shouldRejectNonPositiveDistance(int distance) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(distance)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should reject distance exceeding maximum")
        void shouldRejectExcessiveDistance() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(25000)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 100, 1000, 5000, 15000, 20000})
        @DisplayName("Should accept valid distances")
        void shouldAcceptValidDistances(int distance) {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(distance)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionRequestDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    // ==================== BUSINESS LOGIC TESTS ====================

    @Nested
    @DisplayName("Business Logic Methods")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should convert to uppercase")
        void shouldConvertToUpperCase() {
            // Arrange
            FlightPredictionRequestDTO dto = FlightPredictionRequestDTO.builder()
                    .flightNumber("aa1234")
                    .companyName("aa")
                    .flightOrigin("jfk")
                    .flightDestination("lax")
                    .flightDepartureDate(LocalDateTime.now().plusDays(7))
                    .flightDistance(3974)
                    .build();

            // Act
            FlightPredictionRequestDTO upperCaseDto = dto.toUpperCase();

            // Assert
            assertAll(
                    () -> assertThat(upperCaseDto.getFlightNumber()).isEqualTo("AA1234"),
                    () -> assertThat(upperCaseDto.getCompanyName()).isEqualTo("AA"),
                    () -> assertThat(upperCaseDto.getFlightOrigin()).isEqualTo("JFK"),
                    () -> assertThat(upperCaseDto.getFlightDestination()).isEqualTo("LAX")
            );
        }

        @Test
        @DisplayName("Should detect domestic flight")
        void shouldDetectDomesticFlight() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightOrigin("GIG")
                    .flightDestination("GRU")
                    .build();

            // Act & Assert
            assertThat(dto.isDomesticFlight()).isTrue();
        }

        @Test
        @DisplayName("Should detect international flight")
        void shouldDetectInternationalFlight() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightOrigin("GIG")
                    .flightDestination("JFK")
                    .build();

            // Act & Assert
            assertThat(dto.isDomesticFlight()).isFalse();
        }

        @Test
        @DisplayName("Should categorize short haul flight")
        void shouldCategorizeShortHaulFlight() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(500)
                    .build();

            // Act & Assert
            assertThat(dto.getFlightCategory())
                    .isEqualTo(FlightPredictionRequestDTO.FlightCategory.SHORT_HAUL);
        }

        @Test
        @DisplayName("Should categorize medium haul flight")
        void shouldCategorizeMediumHaulFlight() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(2000)
                    .build();

            // Act & Assert
            assertThat(dto.getFlightCategory())
                    .isEqualTo(FlightPredictionRequestDTO.FlightCategory.MEDIUM_HAUL);
        }

        @Test
        @DisplayName("Should categorize long haul flight")
        void shouldCategorizeLongHaulFlight() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(6000)
                    .build();

            // Act & Assert
            assertThat(dto.getFlightCategory())
                    .isEqualTo(FlightPredictionRequestDTO.FlightCategory.LONG_HAUL);
        }

        @Test
        @DisplayName("Should categorize ultra long haul flight")
        void shouldCategorizeUltraLongHaulFlight() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTOBuilder()
                    .flightDistance(12000)
                    .build();

            // Act & Assert
            assertThat(dto.getFlightCategory())
                    .isEqualTo(FlightPredictionRequestDTO.FlightCategory.ULTRA_LONG_HAUL);
        }
    }

    // ==================== IMMUTABILITY TESTS ====================

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable (using @Value)")
        void shouldBeImmutable() {
            // Arrange
            FlightPredictionRequestDTO dto = createValidDTO();

            // Act - toBuilder creates a new instance
            FlightPredictionRequestDTO modifiedDto = dto.toBuilder()
                    .flightDistance(5000)
                    .build();

            // Assert - original unchanged
            assertThat(dto.getFlightDistance()).isEqualTo(3974);
            assertThat(modifiedDto.getFlightDistance()).isEqualTo(5000);
        }
    }

    // ==================== HELPER METHODS ====================

    private FlightPredictionRequestDTO createValidDTO() {
        return FlightPredictionRequestDTO.builder()
                .flightNumber("AA1234")
                .companyName("AA")
                .flightOrigin("JFK")
                .flightDestination("LAX")
                .flightDepartureDate(LocalDateTime.now().plusDays(7))
                .flightDistance(3974)
                .build();
    }

    private FlightPredictionRequestDTO.FlightPredictionRequestDTOBuilder createValidDTOBuilder() {
        return FlightPredictionRequestDTO.builder()
                .flightNumber("AA1234")
                .companyName("AA")
                .flightOrigin("JFK")
                .flightDestination("LAX")
                .flightDepartureDate(LocalDateTime.now().plusDays(7))
                .flightDistance(3974);
    }
}
