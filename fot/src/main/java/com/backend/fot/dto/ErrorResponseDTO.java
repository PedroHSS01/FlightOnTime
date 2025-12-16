package com.backend.fot.dto;


import java.time.Instant;
import java.util.List;


/**
 * Immutable DTO for standardized error responses.
 *
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-15
 */

public record ErrorResponseDTO(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        List<ErrorFildsDTO> fieldErrors
) {

    public static ErrorResponseDTO of(
            Integer status,
            String error,
            String message,
            List<ErrorFildsDTO> fieldErrors
    ) {
        return new ErrorResponseDTO(
                Instant.now(),
                status,
                error,
                message,
                fieldErrors
        );
    }
}
