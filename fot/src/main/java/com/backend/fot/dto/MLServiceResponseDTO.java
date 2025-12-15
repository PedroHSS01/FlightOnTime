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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Immutable DTO representing the response from Machine Learning service.
 * Uses Value Object pattern for thread-safety and immutability.
 * 
 * @since 1.0
 */
@Schema(description = "Response from ML service containing flight delay prediction and confidence metrics")
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MLServiceResponseDTO {
    
    @Schema(
        description = "Prediction result from ML model (0=ON_TIME, 1=DELAYED)",
        example = "1"
    )
    @NotNull(message = "Prediction cannot be null")
    Integer prediction;
    
    @Schema(
        description = "Confidence score from ML model (0.0 to 1.0)",
        example = "0.92",
        minimum = "0.0",
        maximum = "1.0"
    )
    @NotNull(message = "Confidence cannot be null")
    @DecimalMin(value = "0.0", message = "Confidence must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Confidence must be at most 1.0")
    BigDecimal confidence;
    
    @Schema(
        description = "Model name or version that generated this prediction",
        example = "RandomForest-v2.1"
    )
    String modelVersion;
    
    @Schema(
        description = "Processing time in milliseconds",
        example = "125"
    )
    Long processingTimeMs;

    /**
     * Converts raw prediction integer to FlightPrediction enum.
     *
     * @return FlightPrediction enum value
     * @throws IllegalArgumentException if prediction value is invalid
     */
    public FlightPrediction getPredictionEnum() {
        return FlightPrediction.fromValue(prediction);
    }

    /**
     * Returns confidence as percentage (0-100).
     *
     * @return confidence percentage rounded to 2 decimal places
     */
    public BigDecimal getConfidencePercentage() {
        return confidence.multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns formatted confidence as string with percentage symbol.
     *
     * @return formatted string like "92.50%"
     */
    public String getFormattedConfidence() {
        return getConfidencePercentage() + "%";
    }

    /**
     * Checks if the prediction has high confidence (>= 80%).
     *
     * @return true if confidence is 0.8 or higher
     */
    public boolean isHighConfidence() {
        return confidence.compareTo(new BigDecimal("0.8")) >= 0;
    }

    /**
     * Checks if the prediction has low confidence (< 60%).
     *
     * @return true if confidence is below 0.6
     */
    public boolean isLowConfidence() {
        return confidence.compareTo(new BigDecimal("0.6")) < 0;
    }

    /**
     * Determines confidence level category.
     *
     * @return confidence level enum
     */
    public ConfidenceLevel getConfidenceLevel() {
        if (confidence.compareTo(new BigDecimal("0.95")) >= 0) {
            return ConfidenceLevel.VERY_HIGH;
        } else if (confidence.compareTo(new BigDecimal("0.80")) >= 0) {
            return ConfidenceLevel.HIGH;
        } else if (confidence.compareTo(new BigDecimal("0.60")) >= 0) {
            return ConfidenceLevel.MEDIUM;
        } else if (confidence.compareTo(new BigDecimal("0.40")) >= 0) {
            return ConfidenceLevel.LOW;
        } else {
            return ConfidenceLevel.VERY_LOW;
        }
    }

    /**
     * Creates a human-readable summary of the ML prediction.
     *
     * @return formatted summary string
     */
    public String getSummary() {
        FlightPrediction pred = getPredictionEnum();
        String confidenceStr = getFormattedConfidence();
        ConfidenceLevel level = getConfidenceLevel();
        
        String summary = String.format("ML Prediction: %s with %s confidence (%s)",
                pred.name(), confidenceStr, level.name());
        
        if (modelVersion != null) {
            summary += String.format(" [Model: %s]", modelVersion);
        }
        
        if (processingTimeMs != null) {
            summary += String.format(" [%dms]", processingTimeMs);
        }
        
        return summary;
    }

    /**
     * Checks if prediction suggests flight delay.
     *
     * @return true if prediction is DELAYED
     */
    public boolean isDelayPredicted() {
        return getPredictionEnum() == FlightPrediction.DELAYED;
    }

    /**
     * Checks if prediction suggests flight on time.
     *
     * @return true if prediction is ON_TIME
     */
    public boolean isOnTimePredicted() {
        return getPredictionEnum() == FlightPrediction.ON_TIME;
    }

    /**
     * Checks if this is a reliable prediction based on confidence threshold.
     *
     * @param threshold minimum confidence threshold (0.0 to 1.0)
     * @return true if confidence meets or exceeds threshold
     */
    public boolean isReliable(BigDecimal threshold) {
        return confidence.compareTo(threshold) >= 0;
    }

    /**
     * Enum representing confidence level categories for ML predictions.
     */
    public enum ConfidenceLevel {
        VERY_HIGH("Very High", "≥ 95%"),
        HIGH("High", "≥ 80%"),
        MEDIUM("Medium", "≥ 60%"),
        LOW("Low", "≥ 40%"),
        VERY_LOW("Very Low", "< 40%");

        private final String displayName;
        private final String range;

        ConfidenceLevel(String displayName, String range) {
            this.displayName = displayName;
            this.range = range;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getRange() {
            return range;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
