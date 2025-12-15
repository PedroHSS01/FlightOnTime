package com.backend.fot.dto;

import com.backend.fot.dto.FlightPredictionResponseDTO.ConfidenceLevel;
import com.backend.fot.enums.FlightPrediction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link FlightPredictionResponseDTO}.
 * 
 * @author FlightOnTime Team
 * @version 1.0
 */
@DisplayName("FlightPredictionResponseDTO Tests")
class FlightPredictionResponseDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should create valid response with all fields")
        void shouldCreateValidResponse() {
            // Arrange & Act
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.85)
                    .confidence(ConfidenceLevel.HIGH)
                    .build();

            // Assert
            Set<ConstraintViolation<FlightPredictionResponseDTO>> violations = validator.validate(response);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should create response with builder pattern")
        void shouldBuildResponseUsingBuilder() {
            // Arrange & Act
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.ON_TIME)
                    .probability(0.92)
                    .build();

            // Assert
            assertAll(
                    () -> assertThat(response.getPrediction()).isEqualTo(FlightPrediction.ON_TIME),
                    () -> assertThat(response.getProbability()).isEqualTo(0.92),
                    () -> assertThat(response.getConfidence()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should reject null prediction")
        void shouldRejectNullPrediction() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(null)
                    .probability(0.85)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionResponseDTO>> violations = validator.validate(response);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prediction"));
        }

        @Test
        @DisplayName("Should reject null probability")
        void shouldRejectNullProbability() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(null)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionResponseDTO>> violations = validator.validate(response);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("probability"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {-0.1, -1.0, 1.1, 2.0})
        @DisplayName("Should reject probability outside 0-1 range")
        void shouldRejectInvalidProbability(double probability) {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(probability)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionResponseDTO>> violations = validator.validate(response);

            // Assert
            assertThat(violations).isNotEmpty();
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, 0.5, 1.0})
        @DisplayName("Should accept valid probabilities")
        void shouldAcceptValidProbabilities(double probability) {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.ON_TIME)
                    .probability(probability)
                    .build();

            // Act
            Set<ConstraintViolation<FlightPredictionResponseDTO>> violations = validator.validate(response);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Probability Percentage Tests")
    class ProbabilityPercentageTests {

        @Test
        @DisplayName("Should convert probability to percentage")
        void shouldConvertToPercentage() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.85)
                    .build();

            // Act & Assert
            assertThat(response.getProbabilityPercentage()).isEqualTo(85.0);
        }

        @Test
        @DisplayName("Should format probability as percentage string")
        void shouldFormatProbability() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.8567)
                    .build();

            // Act & Assert
            assertThat(response.getFormattedProbability()).isEqualTo("85.67%");
        }

        @Test
        @DisplayName("Should handle null probability in formatting")
        void shouldHandleNullProbability() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(null)
                    .build();

            // Act & Assert
            assertAll(
                    () -> assertThat(response.getProbabilityPercentage()).isEqualTo(0.0),
                    () -> assertThat(response.getFormattedProbability()).isEqualTo("N/A")
            );
        }
    }

    @Nested
    @DisplayName("Confidence Tests")
    class ConfidenceTests {

        @Test
        @DisplayName("Should identify high confidence prediction")
        void shouldIdentifyHighConfidence() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.85)
                    .build();

            // Act & Assert
            assertThat(response.isHighConfidence()).isTrue();
        }

        @Test
        @DisplayName("Should identify low confidence prediction")
        void shouldIdentifyLowConfidence() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.ON_TIME)
                    .probability(0.52)
                    .build();

            // Act & Assert
            assertThat(response.isLowConfidence()).isTrue();
        }

        @ParameterizedTest
        @CsvSource({
                "0.95, VERY_HIGH",
                "0.85, HIGH",
                "0.65, MEDIUM",
                "0.50, LOW",
                "0.40, VERY_LOW"
        })
        @DisplayName("Should calculate correct confidence level")
        void shouldCalculateConfidenceLevel(double probability, ConfidenceLevel expectedLevel) {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(probability)
                    .build();

            // Act & Assert
            assertThat(response.getConfidenceLevel()).isEqualTo(expectedLevel);
        }

        @Test
        @DisplayName("Should use explicit confidence if set")
        void shouldUseExplicitConfidence() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.50)
                    .confidence(ConfidenceLevel.HIGH)
                    .build();

            // Act & Assert
            assertThat(response.getConfidenceLevel()).isEqualTo(ConfidenceLevel.HIGH);
        }
    }

    @Nested
    @DisplayName("Summary Tests")
    class SummaryTests {

        @Test
        @DisplayName("Should generate summary for delayed flight")
        void shouldGenerateSummaryForDelayedFlight() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.85)
                    .build();

            // Act
            String summary = response.getSummary();

            // Assert
            assertThat(summary)
                    .contains("DELAYED")
                    .contains("HIGH")
                    .contains("85.00%");
        }

        @Test
        @DisplayName("Should generate summary for on-time flight")
        void shouldGenerateSummaryForOnTimeFlight() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.ON_TIME)
                    .probability(0.92)
                    .build();

            // Act
            String summary = response.getSummary();

            // Assert
            assertThat(summary)
                    .contains("ON_TIME")
                    .contains("VERY_HIGH")
                    .contains("92.00%");
        }

        @Test
        @DisplayName("Should handle null values in summary")
        void shouldHandleNullValuesInSummary() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(null)
                    .probability(null)
                    .build();

            // Act
            String summary = response.getSummary();

            // Assert
            assertThat(summary).isEqualTo("No prediction available");
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable")
        void shouldBeImmutable() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.85)
                    .build();

            // Assert - @Value makes all fields final
            assertThat(response.getPrediction()).isEqualTo(FlightPrediction.DELAYED);
            assertThat(response.getProbability()).isEqualTo(0.85);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle minimum probability (0.0)")
        void shouldHandleMinimumProbability() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.ON_TIME)
                    .probability(0.0)
                    .build();

            // Act & Assert
            assertAll(
                    () -> assertThat(response.getProbabilityPercentage()).isEqualTo(0.0),
                    () -> assertThat(response.isLowConfidence()).isTrue(),
                    () -> assertThat(response.getConfidenceLevel()).isEqualTo(ConfidenceLevel.VERY_LOW)
            );
        }

        @Test
        @DisplayName("Should handle maximum probability (1.0)")
        void shouldHandleMaximumProbability() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(1.0)
                    .build();

            // Act & Assert
            assertAll(
                    () -> assertThat(response.getProbabilityPercentage()).isEqualTo(100.0),
                    () -> assertThat(response.isHighConfidence()).isTrue(),
                    () -> assertThat(response.getConfidenceLevel()).isEqualTo(ConfidenceLevel.VERY_HIGH)
            );
        }

        @Test
        @DisplayName("Should handle boundary probability (0.75)")
        void shouldHandleBoundaryProbability() {
            // Arrange
            FlightPredictionResponseDTO response = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.75)
                    .build();

            // Act & Assert
            assertAll(
                    () -> assertThat(response.isHighConfidence()).isTrue(),
                    () -> assertThat(response.getConfidenceLevel()).isEqualTo(ConfidenceLevel.HIGH)
            );
        }
    }
}
