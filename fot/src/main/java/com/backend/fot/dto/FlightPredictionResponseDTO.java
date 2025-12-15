package com.backend.fot.dto;

import com.backend.fot.enums.FlightPrediction;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Locale;

/**
 * Immutable DTO for flight delay prediction responses.
 * Thread-safe and includes prediction confidence metrics.
 * 
 * @author FlightOnTime Team
 * @version 2.0
 */
@Schema(
    description = "Response payload containing flight delay prediction results and confidence score",
    example = """
        {
          "prediction": "DELAYED",
          "probability": 0.85,
          "confidence": "HIGH"
        }
        """
)
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightPredictionResponseDTO {

    @Schema(
        description = "Predicted outcome for the flight",
        example = "DELAYED",
        allowableValues = {"ON_TIME", "DELAYED"}
    )
    @NotNull
    FlightPrediction prediction;
    
    @Schema(
        description = "Confidence score of the prediction (0.0 to 1.0)",
        example = "0.85",
        minimum = "0.0",
        maximum = "1.0"
    )
    @NotNull
    @DecimalMin(value = "0.0", message = "Probability must be between 0.0 and 1.0")
    @DecimalMax(value = "1.0", message = "Probability must be between 0.0 and 1.0")
    Double probability;

    @Schema(
        description = "Human-readable confidence level based on probability",
        example = "HIGH",
        allowableValues = {"VERY_LOW", "LOW", "MEDIUM", "HIGH", "VERY_HIGH"}
    )
    ConfidenceLevel confidence;

    /**
     * Returns the probability as a percentage (0-100).
     */
    public double getProbabilityPercentage() {
        return probability != null ? probability * 100 : 0.0;
    }

    /**
     * Returns a formatted probability string with percentage.
     */
    public String getFormattedProbability() {
        return probability != null ? String.format(Locale.US, "%.2f%%", getProbabilityPercentage()) : "N/A";
    }

    /**
     * Checks if the prediction has high confidence (>= 0.75).
     */
    public boolean isHighConfidence() {
        return probability != null && probability >= 0.75;
    }

    /**
     * Checks if the prediction has low confidence (<= 0.55).
     */
    public boolean isLowConfidence() {
        return probability != null && probability <= 0.55;
    }

    /**
     * Returns a human-readable summary of the prediction.
     */
    public String getSummary() {
        if (prediction == null || probability == null) {
            return "No prediction available";
        }
        return String.format("Flight is predicted to be %s with %s confidence (%s)",
                prediction.name(),
                confidence != null ? confidence : getConfidenceLevel(),
                getFormattedProbability());
    }

    /**
     * Calculates confidence level from probability if not explicitly set.
     */
    public ConfidenceLevel getConfidenceLevel() {
        if (confidence != null) {
            return confidence;
        }
        if (probability == null) {
            return ConfidenceLevel.UNKNOWN;
        }
        if (probability >= 0.90) {
            return ConfidenceLevel.VERY_HIGH;
        } else if (probability >= 0.75) {
            return ConfidenceLevel.HIGH;
        } else if (probability >= 0.60) {
            return ConfidenceLevel.MEDIUM;
        } else if (probability >= 0.45) {
            return ConfidenceLevel.LOW;
        } else {
            return ConfidenceLevel.VERY_LOW;
        }
    }

    /**
     * Confidence level categories for predictions.
     */
    public enum ConfidenceLevel {
        VERY_HIGH,  // >= 90%
        HIGH,       // >= 75%
        MEDIUM,     // >= 60%
        LOW,        // >= 45%
        VERY_LOW,   // < 45%
        UNKNOWN
    }
}
