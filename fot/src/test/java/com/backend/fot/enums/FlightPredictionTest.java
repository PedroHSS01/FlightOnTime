package com.backend.fot.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("FlightPrediction Enum Tests")
class FlightPredictionTest {

    @Nested
    @DisplayName("Value Mapping Tests")
    class ValueMappingTests {

        @Test
        @DisplayName("Should map 0 to ON_TIME")
        void shouldMapZeroToOnTime() {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.fromValue(0);

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.ON_TIME);
            assertThat(result.getValue()).isZero();
        }

        @Test
        @DisplayName("Should map 1 to DELAYED")
        void shouldMapOneToDelayed() {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.fromValue(1);

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.DELAYED);
            assertThat(result.getValue()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception for invalid value 2")
        void shouldThrowExceptionForInvalidValue() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromValue(2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid prediction value: 2")
                    .hasMessageContaining("Expected 0 (ON_TIME) or 1 (DELAYED)");
        }

        @Test
        @DisplayName("Should throw exception for negative value")
        void shouldThrowExceptionForNegativeValue() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromValue(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid prediction value: -1");
        }

        @Test
        @DisplayName("Should throw exception for large invalid value")
        void shouldThrowExceptionForLargeValue() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromValue(999))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid prediction value: 999");
        }
    }

    @Nested
    @DisplayName("Safe Value Mapping Tests")
    class SafeValueMappingTests {

        @Test
        @DisplayName("Should return Optional with ON_TIME for value 0")
        void shouldReturnOptionalWithOnTime() {
            // Arrange & Act
            Optional<FlightPrediction> result = FlightPrediction.fromValueSafe(0);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(FlightPrediction.ON_TIME);
        }

        @Test
        @DisplayName("Should return Optional with DELAYED for value 1")
        void shouldReturnOptionalWithDelayed() {
            // Arrange & Act
            Optional<FlightPrediction> result = FlightPrediction.fromValueSafe(1);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(FlightPrediction.DELAYED);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 2, 5, 10, 999})
        @DisplayName("Should return empty Optional for invalid values")
        void shouldReturnEmptyOptionalForInvalidValues(int value) {
            // Arrange & Act
            Optional<FlightPrediction> result = FlightPrediction.fromValueSafe(value);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("String Parsing Tests")
    class StringParsingTests {

        @ParameterizedTest
        @CsvSource({
                "ON_TIME, ON_TIME",
                "on_time, ON_TIME",
                "On_Time, ON_TIME",
                "ON TIME, ON_TIME",
                "on time, ON_TIME",
                "DELAYED, DELAYED",
                "delayed, DELAYED",
                "Delayed, DELAYED"
        })
        @DisplayName("Should parse various string formats")
        void shouldParseVariousStringFormats(String input, FlightPrediction expected) {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.fromString(input);

            // Assert
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should throw exception for null string")
        void shouldThrowExceptionForNullString() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromString(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception for empty string")
        void shouldThrowExceptionForEmptyString() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromString(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception for whitespace-only string")
        void shouldThrowExceptionForWhitespaceString() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromString("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null or empty");
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID", "LATE", "EARLY", "CANCELLED", "123"})
        @DisplayName("Should throw exception for invalid strings")
        void shouldThrowExceptionForInvalidStrings(String input) {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromString(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid prediction text");
        }
    }

    @Nested
    @DisplayName("Safe String Parsing Tests")
    class SafeStringParsingTests {

        @ParameterizedTest
        @CsvSource({
                "ON_TIME, ON_TIME",
                "DELAYED, DELAYED",
                "on time, ON_TIME",
                "delayed, DELAYED"
        })
        @DisplayName("Should parse valid strings to Optional")
        void shouldParseValidStringsToOptional(String input, FlightPrediction expected) {
            // Arrange & Act
            Optional<FlightPrediction> result = FlightPrediction.fromStringSafe(input);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID", "LATE", "", "   ", "123"})
        @DisplayName("Should return empty Optional for invalid strings")
        void shouldReturnEmptyOptionalForInvalidStrings(String input) {
            // Arrange & Act
            Optional<FlightPrediction> result = FlightPrediction.fromStringSafe(input);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional for null string")
        void shouldReturnEmptyOptionalForNullString() {
            // Arrange & Act
            Optional<FlightPrediction> result = FlightPrediction.fromStringSafe(null);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Boolean Check Tests")
    class BooleanCheckTests {

        @Test
        @DisplayName("ON_TIME should return true for isOnTime()")
        void onTimeShouldReturnTrueForIsOnTime() {
            // Arrange & Act & Assert
            assertThat(FlightPrediction.ON_TIME.isOnTime()).isTrue();
            assertThat(FlightPrediction.ON_TIME.isDelayed()).isFalse();
        }

        @Test
        @DisplayName("DELAYED should return true for isDelayed()")
        void delayedShouldReturnTrueForIsDelayed() {
            // Arrange & Act & Assert
            assertThat(FlightPrediction.DELAYED.isDelayed()).isTrue();
            assertThat(FlightPrediction.DELAYED.isOnTime()).isFalse();
        }
    }

    @Nested
    @DisplayName("Negate Tests")
    class NegateTests {

        @Test
        @DisplayName("Should negate ON_TIME to DELAYED")
        void shouldNegateOnTimeToDelayed() {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.ON_TIME.negate();

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.DELAYED);
        }

        @Test
        @DisplayName("Should negate DELAYED to ON_TIME")
        void shouldNegateDelayedToOnTime() {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.DELAYED.negate();

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.ON_TIME);
        }

        @Test
        @DisplayName("Double negation should return original value")
        void doubleNegationShouldReturnOriginal() {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.ON_TIME.negate().negate();

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.ON_TIME);
        }
    }

    @Nested
    @DisplayName("Display Properties Tests")
    class DisplayPropertiesTests {

        @Test
        @DisplayName("ON_TIME should have correct properties")
        void onTimeShouldHaveCorrectProperties() {
            // Arrange
            FlightPrediction prediction = FlightPrediction.ON_TIME;

            // Act & Assert
            assertThat(prediction.getValue()).isZero();
            assertThat(prediction.getDisplayName()).isEqualTo("On Time");
            assertThat(prediction.getDescription()).contains("within scheduled time");
            assertThat(prediction.getSymbol()).isEqualTo("✓");
        }

        @Test
        @DisplayName("DELAYED should have correct properties")
        void delayedShouldHaveCorrectProperties() {
            // Arrange
            FlightPrediction prediction = FlightPrediction.DELAYED;

            // Act & Assert
            assertThat(prediction.getValue()).isEqualTo(1);
            assertThat(prediction.getDisplayName()).isEqualTo("Delayed");
            assertThat(prediction.getDescription()).contains("arrive late");
            assertThat(prediction.getSymbol()).isEqualTo("✗");
        }
    }

    @Nested
    @DisplayName("Formatted Display Tests")
    class FormattedDisplayTests {

        @Test
        @DisplayName("Should format ON_TIME with symbol and display name")
        void shouldFormatOnTimeWithSymbol() {
            // Arrange & Act
            String result = FlightPrediction.ON_TIME.getFormattedDisplay();

            // Assert
            assertThat(result).isEqualTo("✓ On Time");
        }

        @Test
        @DisplayName("Should format DELAYED with symbol and display name")
        void shouldFormatDelayedWithSymbol() {
            // Arrange & Act
            String result = FlightPrediction.DELAYED.getFormattedDisplay();

            // Assert
            assertThat(result).isEqualTo("✗ Delayed");
        }
    }

    @Nested
    @DisplayName("Detailed Info Tests")
    class DetailedInfoTests {

        @Test
        @DisplayName("Should return detailed info for ON_TIME")
        void shouldReturnDetailedInfoForOnTime() {
            // Arrange & Act
            String result = FlightPrediction.ON_TIME.getDetailedInfo();

            // Assert
            assertThat(result)
                    .contains("[0]")
                    .contains("On Time")
                    .contains("within scheduled time");
        }

        @Test
        @DisplayName("Should return detailed info for DELAYED")
        void shouldReturnDetailedInfoForDelayed() {
            // Arrange & Act
            String result = FlightPrediction.DELAYED.getDetailedInfo();

            // Assert
            assertThat(result)
                    .contains("[1]")
                    .contains("Delayed")
                    .contains("arrive late");
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString() should return display name for ON_TIME")
        void toStringShouldReturnDisplayNameForOnTime() {
            // Arrange & Act
            String result = FlightPrediction.ON_TIME.toString();

            // Assert
            assertThat(result).isEqualTo("On Time");
        }

        @Test
        @DisplayName("toString() should return display name for DELAYED")
        void toStringShouldReturnDisplayNameForDelayed() {
            // Arrange & Act
            String result = FlightPrediction.DELAYED.toString();

            // Assert
            assertThat(result).isEqualTo("Delayed");
        }
    }

    @Nested
    @DisplayName("Enum Values Tests")
    class EnumValuesTests {

        @Test
        @DisplayName("Should have exactly 2 enum values")
        void shouldHaveExactlyTwoValues() {
            // Arrange & Act
            FlightPrediction[] values = FlightPrediction.values();

            // Assert
            assertThat(values).hasSize(2);
        }

        @Test
        @DisplayName("Should contain ON_TIME and DELAYED")
        void shouldContainOnTimeAndDelayed() {
            // Arrange & Act
            FlightPrediction[] values = FlightPrediction.values();

            // Assert
            assertThat(values).containsExactly(
                    FlightPrediction.ON_TIME,
                    FlightPrediction.DELAYED
            );
        }

        @Test
        @DisplayName("Should be able to valueOf ON_TIME")
        void shouldBeAbleToValueOfOnTime() {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.valueOf("ON_TIME");

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.ON_TIME);
        }

        @Test
        @DisplayName("Should be able to valueOf DELAYED")
        void shouldBeAbleToValueOfDelayed() {
            // Arrange & Act
            FlightPrediction result = FlightPrediction.valueOf("DELAYED");

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.DELAYED);
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("fromValue should use optimized Map lookup")
        void fromValueShouldUseOptimizedMapLookup() {
            // Arrange - measure time for multiple lookups
            long startTime = System.nanoTime();

            // Act - perform many lookups
            for (int i = 0; i < 10000; i++) {
                FlightPrediction.fromValue(0);
                FlightPrediction.fromValue(1);
            }

            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            // Assert - should be very fast (< 100ms for 20k lookups)
            assertThat(durationMs).isLessThan(100);
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Enum values should be immutable")
        void enumValuesShouldBeImmutable() {
            // Arrange
            FlightPrediction prediction = FlightPrediction.ON_TIME;

            // Act - get value multiple times
            int value1 = prediction.getValue();
            int value2 = prediction.getValue();

            // Assert - should always return same value
            assertThat(value1).isEqualTo(value2);
            assertThat(prediction.getValue()).isZero();
        }

        @Test
        @DisplayName("Enum should not have setters")
        void enumShouldNotHaveSetters() {
            // Arrange & Act & Assert
            assertThat(FlightPrediction.class.getMethods())
                    .extracting("name")
                    .filteredOn(name -> ((String) name).startsWith("set"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle Integer.MAX_VALUE")
        void shouldHandleMaxIntValue() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromValue(Integer.MAX_VALUE))
                    .isInstanceOf(IllegalArgumentException.class);
            
            assertThat(FlightPrediction.fromValueSafe(Integer.MAX_VALUE)).isEmpty();
        }

        @Test
        @DisplayName("Should handle Integer.MIN_VALUE")
        void shouldHandleMinIntValue() {
            // Act & Assert
            assertThatThrownBy(() -> FlightPrediction.fromValue(Integer.MIN_VALUE))
                    .isInstanceOf(IllegalArgumentException.class);
            
            assertThat(FlightPrediction.fromValueSafe(Integer.MIN_VALUE)).isEmpty();
        }

        @Test
        @DisplayName("Should handle strings with multiple spaces")
        void shouldHandleStringsWithMultipleSpaces() {
            // Act
            FlightPrediction result = FlightPrediction.fromString("  ON   TIME  ");

            // Assert
            assertThat(result).isEqualTo(FlightPrediction.ON_TIME);
        }
    }
}
