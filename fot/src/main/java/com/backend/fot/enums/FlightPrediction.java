package com.backend.fot.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Flight prediction outcome", enumAsRef = true)
public enum FlightPrediction {

    @Schema(description = "Flight is predicted to be on time (value: 0)")
    ON_TIME(0),

    @Schema(description = "Flight is predicted to be delayed (value: 1)")
    DELAYED(1);

    private final int value;

    FlightPrediction(int value) {
        this.value = value;
    }

    @Schema(description = "Integer value associated with this prediction (0 for ON_TIME, 1 for DELAYED)")
    public int getValue() {
        return value;
    }

    /**
     * @param value ( 0 or 1 )
     * @return FlightPrediction corresponding to the given integer value (0 for
     *         ON_TIME, 1 for DELAYED)
     * @throws IllegalArgumentException if the value does not correspond to any
     *                                  prediction
     */
    public static FlightPrediction fromValue(int value) {
        for (FlightPrediction prediction : FlightPrediction.values()) {
            if (prediction.value == value) {
                return prediction;
            }
        }
        throw new IllegalArgumentException("Invalid prediction value: " + value);
    }
}
