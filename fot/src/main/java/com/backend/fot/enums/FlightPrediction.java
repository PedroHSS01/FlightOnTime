package com.backend.fot.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enum representing the possible outcomes of a flight delay prediction.
 * Uses immutable value mapping for ML model integration.
 * 
 * @since 1.0
 */
@Schema(description = "Flight prediction outcome from ML model", enumAsRef = true)
public enum FlightPrediction {

    @Schema(description = "Flight is predicted to arrive on time with no significant delays")
    ON_TIME(0, "On Time", "Flight expected to arrive within scheduled time", "✓"),

    @Schema(description = "Flight is predicted to experience delays beyond acceptable threshold")
    DELAYED(1, "Delayed", "Flight expected to arrive late", "✗");

    private static final Map<Integer, FlightPrediction> VALUE_MAP = 
            Arrays.stream(values())
                  .collect(Collectors.toMap(FlightPrediction::getValue, Function.identity()));

    private final int value;
    private final String displayName;
    private final String description;
    private final String symbol;

    /**
     * Constructor for FlightPrediction enum.
     *
     * @param value ML model output value (0 or 1)
     * @param displayName human-readable name
     * @param description detailed description
     * @param symbol visual indicator
     */
    FlightPrediction(int value, String displayName, String description, String symbol) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
        this.symbol = symbol;
    }

    /**
     * Gets the integer value used by ML model.
     *
     * @return 0 for ON_TIME, 1 for DELAYED
     */
    @JsonValue
    @Schema(description = "Integer value for ML model (0=ON_TIME, 1=DELAYED)")
    public int getValue() {
        return value;
    }

    /**
     * Gets the human-readable display name.
     *
     * @return display name like "On Time" or "Delayed"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the detailed description.
     *
     * @return description of what this prediction means
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the visual symbol for UI representation.
     *
     * @return symbol like "✓" or "✗"
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Checks if this prediction indicates the flight is on time.
     *
     * @return true if ON_TIME, false otherwise
     */
    public boolean isOnTime() {
        return this == ON_TIME;
    }

    /**
     * Checks if this prediction indicates the flight is delayed.
     *
     * @return true if DELAYED, false otherwise
     */
    public boolean isDelayed() {
        return this == DELAYED;
    }

    /**
     * Gets the opposite prediction.
     *
     * @return DELAYED if ON_TIME, ON_TIME if DELAYED
     */
    public FlightPrediction negate() {
        return this == ON_TIME ? DELAYED : ON_TIME;
    }

    /**
     * Converts integer value to FlightPrediction enum using optimized lookup.
     * This method is used by Jackson for JSON deserialization.
     *
     * @param value ML model output (0 or 1)
     * @return corresponding FlightPrediction enum
     * @throws IllegalArgumentException if value is not 0 or 1
     */
    @JsonCreator
    public static FlightPrediction fromValue(int value) {
        FlightPrediction prediction = VALUE_MAP.get(value);
        if (prediction == null) {
            throw new IllegalArgumentException(
                    String.format("Invalid prediction value: %d. Expected 0 (ON_TIME) or 1 (DELAYED)", value)
            );
        }
        return prediction;
    }

    /**
     * Safely converts integer value to Optional FlightPrediction.
     * Returns empty Optional instead of throwing exception for invalid values.
     *
     * @param value ML model output
     * @return Optional containing FlightPrediction, or empty if invalid
     */
    public static Optional<FlightPrediction> fromValueSafe(int value) {
        return Optional.ofNullable(VALUE_MAP.get(value));
    }

    /**
     * Parses string representation to FlightPrediction.
     * Case-insensitive and handles both enum names and display names.
     *
     * @param text string to parse (e.g., "ON_TIME", "on time", "Delayed")
     * @return corresponding FlightPrediction
     * @throws IllegalArgumentException if text cannot be parsed
     */
    public static FlightPrediction fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Prediction text cannot be null or empty");
        }

        // Normalize: trim, uppercase, replace multiple spaces with single underscore
        String normalized = text.trim()
                .toUpperCase()
                .replaceAll("\\s+", "_");

        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid prediction text: '%s'. Expected 'ON_TIME' or 'DELAYED'", text)
            );
        }
    }

    /**
     * Safely parses string representation to Optional FlightPrediction.
     *
     * @param text string to parse
     * @return Optional containing FlightPrediction, or empty if invalid
     */
    public static Optional<FlightPrediction> fromStringSafe(String text) {
        try {
            return Optional.of(fromString(text));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a formatted string with symbol and display name.
     *
     * @return formatted string like "✓ On Time"
     */
    public String getFormattedDisplay() {
        return symbol + " " + displayName;
    }

    /**
     * Gets a detailed information string.
     *
     * @return string with value, name, and description
     */
    public String getDetailedInfo() {
        return String.format("[%d] %s: %s", value, displayName, description);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
