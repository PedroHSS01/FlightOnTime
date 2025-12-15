package com.backend.fot.dto;

import com.backend.fot.enums.FlightPrediction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MLServiceResponseDTO Tests")
class MLServiceResponseDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should create valid DTO with all required fields")
        void shouldCreateValidDTOWithRequiredFields() {
            // Arrange & Act
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.92"))
                    .build();

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getPrediction()).isEqualTo(1);
            assertThat(dto.getConfidence()).isEqualByComparingTo("0.92");
            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("Should create valid DTO with all fields including optional ones")
        void shouldCreateValidDTOWithAllFields() {
            // Arrange & Act
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.85"))
                    .modelVersion("RandomForest-v2.1")
                    .processingTimeMs(125L)
                    .build();

            // Assert
            assertThat(dto).isNotNull();
            assertThat(dto.getPrediction()).isZero();
            assertThat(dto.getConfidence()).isEqualByComparingTo("0.85");
            assertThat(dto.getModelVersion()).isEqualTo("RandomForest-v2.1");
            assertThat(dto.getProcessingTimeMs()).isEqualTo(125L);
            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("Should create DTO with minimum valid confidence (0.0)")
        void shouldCreateDTOWithMinimumConfidence() {
            // Arrange & Act
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(BigDecimal.ZERO)
                    .build();

            // Assert
            assertThat(dto.getConfidence()).isEqualByComparingTo("0.0");
            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("Should create DTO with maximum valid confidence (1.0)")
        void shouldCreateDTOWithMaximumConfidence() {
            // Arrange & Act
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(BigDecimal.ONE)
                    .build();

            // Assert
            assertThat(dto.getConfidence()).isEqualByComparingTo("1.0");
            assertThat(validator.validate(dto)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should fail validation when prediction is null")
        void shouldFailValidationWhenPredictionIsNull() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(null)
                    .confidence(new BigDecimal("0.92"))
                    .build();

            // Act
            Set<ConstraintViolation<MLServiceResponseDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly("Prediction cannot be null");
        }

        @Test
        @DisplayName("Should fail validation when confidence is null")
        void shouldFailValidationWhenConfidenceIsNull() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(null)
                    .build();

            // Act
            Set<ConstraintViolation<MLServiceResponseDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly("Confidence cannot be null");
        }

        @Test
        @DisplayName("Should fail validation when confidence is below minimum (< 0.0)")
        void shouldFailValidationWhenConfidenceBelowMinimum() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("-0.1"))
                    .build();

            // Act
            Set<ConstraintViolation<MLServiceResponseDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly("Confidence must be at least 0.0");
        }

        @Test
        @DisplayName("Should fail validation when confidence is above maximum (> 1.0)")
        void shouldFailValidationWhenConfidenceAboveMaximum() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("1.5"))
                    .build();

            // Act
            Set<ConstraintViolation<MLServiceResponseDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly("Confidence must be at most 1.0");
        }

        @Test
        @DisplayName("Should fail validation when both prediction and confidence are null")
        void shouldFailValidationWhenBothFieldsAreNull() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder().build();

            // Act
            Set<ConstraintViolation<MLServiceResponseDTO>> violations = validator.validate(dto);

            // Assert
            assertThat(violations).hasSize(2);
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder(
                            "Prediction cannot be null",
                            "Confidence cannot be null"
                    );
        }
    }

    @Nested
    @DisplayName("Prediction Enum Conversion Tests")
    class PredictionEnumTests {

        @Test
        @DisplayName("Should convert prediction 0 to ON_TIME enum")
        void shouldConvertZeroToOnTime() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.85"))
                    .build();

            // Act
            FlightPrediction result = dto.getPredictionEnum();

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.ON_TIME);
        }

        @Test
        @DisplayName("Should convert prediction 1 to DELAYED enum")
        void shouldConvertOneToDelayed() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.92"))
                    .build();

            // Act
            FlightPrediction result = dto.getPredictionEnum();

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.DELAYED);
        }

        @Test
        @DisplayName("Should throw exception for invalid prediction value")
        void shouldThrowExceptionForInvalidPrediction() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(999)
                    .confidence(new BigDecimal("0.5"))
                    .build();

            // Act & Assert
            assertThatThrownBy(dto::getPredictionEnum)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Confidence Percentage Tests")
    class ConfidencePercentageTests {

        @ParameterizedTest
        @CsvSource({
                "0.00, 0.00",
                "0.25, 25.00",
                "0.50, 50.00",
                "0.75, 75.00",
                "0.85, 85.00",
                "0.92, 92.00",
                "1.00, 100.00"
        })
        @DisplayName("Should convert confidence to percentage correctly")
        void shouldConvertConfidenceToPercentage(String confidence, String expectedPercentage) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act
            BigDecimal result = dto.getConfidencePercentage();

            // Assert
            assertThat(result).isEqualByComparingTo(expectedPercentage);
        }

        @Test
        @DisplayName("Should round confidence percentage to 2 decimal places")
        void shouldRoundConfidencePercentageToTwoDecimals() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.8542"))
                    .build();

            // Act
            BigDecimal result = dto.getConfidencePercentage();

            // Assert
            assertThat(result).isEqualByComparingTo("85.42");
        }

        @ParameterizedTest
        @CsvSource({
                "0.00, 0.00%",
                "0.50, 50.00%",
                "0.8542, 85.42%",
                "0.92, 92.00%",
                "1.00, 100.00%"
        })
        @DisplayName("Should format confidence with percentage symbol")
        void shouldFormatConfidenceWithPercentageSymbol(String confidence, String expected) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act
            String result = dto.getFormattedConfidence();

            // Assert
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Confidence Level Tests")
    class ConfidenceLevelTests {

        @ParameterizedTest
        @ValueSource(strings = {"0.95", "0.96", "0.99", "1.00"})
        @DisplayName("Should return VERY_HIGH confidence level (>= 0.95)")
        void shouldReturnVeryHighConfidenceLevel(String confidence) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act & Assert
            assertThat(dto.getConfidenceLevel())
                    .isEqualTo(MLServiceResponseDTO.ConfidenceLevel.VERY_HIGH);
            assertThat(dto.isHighConfidence()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.80", "0.85", "0.90", "0.94"})
        @DisplayName("Should return HIGH confidence level (>= 0.80)")
        void shouldReturnHighConfidenceLevel(String confidence) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act & Assert
            assertThat(dto.getConfidenceLevel())
                    .isEqualTo(MLServiceResponseDTO.ConfidenceLevel.HIGH);
            assertThat(dto.isHighConfidence()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.60", "0.65", "0.70", "0.79"})
        @DisplayName("Should return MEDIUM confidence level (>= 0.60)")
        void shouldReturnMediumConfidenceLevel(String confidence) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act & Assert
            assertThat(dto.getConfidenceLevel())
                    .isEqualTo(MLServiceResponseDTO.ConfidenceLevel.MEDIUM);
            assertThat(dto.isHighConfidence()).isFalse();
            assertThat(dto.isLowConfidence()).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.40", "0.45", "0.50", "0.59"})
        @DisplayName("Should return LOW confidence level (>= 0.40)")
        void shouldReturnLowConfidenceLevel(String confidence) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act & Assert
            assertThat(dto.getConfidenceLevel())
                    .isEqualTo(MLServiceResponseDTO.ConfidenceLevel.LOW);
            assertThat(dto.isLowConfidence()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.00", "0.10", "0.25", "0.39"})
        @DisplayName("Should return VERY_LOW confidence level (< 0.40)")
        void shouldReturnVeryLowConfidenceLevel(String confidence) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act & Assert
            assertThat(dto.getConfidenceLevel())
                    .isEqualTo(MLServiceResponseDTO.ConfidenceLevel.VERY_LOW);
            assertThat(dto.isLowConfidence()).isTrue();
        }
    }

    @Nested
    @DisplayName("Summary Tests")
    class SummaryTests {

        @Test
        @DisplayName("Should generate summary with minimal information")
        void shouldGenerateSummaryWithMinimalInfo() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.92"))
                    .build();

            // Act
            String summary = dto.getSummary();

            // Assert
            assertThat(summary)
                    .contains("ML Prediction: DELAYED")
                    .contains("92.00%")
                    .contains("HIGH")
                    .doesNotContain("Model:")
                    .doesNotContain("ms");
        }

        @Test
        @DisplayName("Should generate summary with model version")
        void shouldGenerateSummaryWithModelVersion() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.85"))
                    .modelVersion("RandomForest-v2.1")
                    .build();

            // Act
            String summary = dto.getSummary();

            // Assert
            assertThat(summary)
                    .contains("ML Prediction: ON_TIME")
                    .contains("85.00%")
                    .contains("HIGH")
                    .contains("[Model: RandomForest-v2.1]");
        }

        @Test
        @DisplayName("Should generate summary with processing time")
        void shouldGenerateSummaryWithProcessingTime() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.78"))
                    .processingTimeMs(125L)
                    .build();

            // Act
            String summary = dto.getSummary();

            // Assert
            assertThat(summary)
                    .contains("ML Prediction: DELAYED")
                    .contains("78.00%")
                    .contains("MEDIUM")
                    .contains("[125ms]");
        }

        @Test
        @DisplayName("Should generate complete summary with all fields")
        void shouldGenerateCompleteSummaryWithAllFields() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.96"))
                    .modelVersion("XGBoost-v3.0")
                    .processingTimeMs(87L)
                    .build();

            // Act
            String summary = dto.getSummary();

            // Assert
            assertThat(summary)
                    .isEqualTo("ML Prediction: ON_TIME with 96.00% confidence (VERY_HIGH) [Model: XGBoost-v3.0] [87ms]");
        }
    }

    @Nested
    @DisplayName("Prediction Check Tests")
    class PredictionCheckTests {

        @Test
        @DisplayName("Should return true when delay is predicted")
        void shouldReturnTrueWhenDelayPredicted() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.85"))
                    .build();

            // Act & Assert
            assertThat(dto.isDelayPredicted()).isTrue();
            assertThat(dto.isOnTimePredicted()).isFalse();
        }

        @Test
        @DisplayName("Should return true when on-time is predicted")
        void shouldReturnTrueWhenOnTimePredicted() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.92"))
                    .build();

            // Act & Assert
            assertThat(dto.isOnTimePredicted()).isTrue();
            assertThat(dto.isDelayPredicted()).isFalse();
        }
    }

    @Nested
    @DisplayName("Reliability Tests")
    class ReliabilityTests {

        @ParameterizedTest
        @CsvSource({
                "0.85, 0.80, true",
                "0.85, 0.85, true",
                "0.85, 0.90, false",
                "0.95, 0.95, true",
                "0.50, 0.60, false"
        })
        @DisplayName("Should check reliability against threshold")
        void shouldCheckReliabilityAgainstThreshold(String confidence, String threshold, boolean expected) {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal(confidence))
                    .build();

            // Act
            boolean result = dto.isReliable(new BigDecimal(threshold));

            // Assert
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable - no setters available")
        void shouldBeImmutable() {
            // Arrange & Act
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.92"))
                    .build();

            // Assert - verify no setter methods exist
            assertThat(dto.getClass().getMethods())
                    .extracting("name")
                    .filteredOn(name -> ((String) name).startsWith("set"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Should create new instance with builder using different values")
        void shouldCreateNewInstanceWithBuilder() {
            // Arrange
            MLServiceResponseDTO dto1 = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.85"))
                    .build();

            // Act
            MLServiceResponseDTO dto2 = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.92"))
                    .build();

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
            assertThat(dto1.getPrediction()).isNotEqualTo(dto2.getPrediction());
            assertThat(dto1.getConfidence()).isNotEqualByComparingTo(dto2.getConfidence());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very small confidence values")
        void shouldHandleVerySmallConfidence() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.0001"))
                    .build();

            // Act & Assert
            assertThat(dto.getConfidencePercentage()).isEqualByComparingTo("0.01");
            assertThat(dto.isLowConfidence()).isTrue();
            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("Should handle very large processing times")
        void shouldHandleVeryLargeProcessingTime() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.75"))
                    .processingTimeMs(999999L)
                    .build();

            // Act
            String summary = dto.getSummary();

            // Assert
            assertThat(summary).contains("[999999ms]");
        }

        @Test
        @DisplayName("Should handle model version with special characters")
        void shouldHandleModelVersionWithSpecialCharacters() {
            // Arrange
            MLServiceResponseDTO dto = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.88"))
                    .modelVersion("ML-Model_v2.1-BETA (2025)")
                    .build();

            // Act
            String summary = dto.getSummary();

            // Assert
            assertThat(summary).contains("[Model: ML-Model_v2.1-BETA (2025)]");
        }
    }
}
