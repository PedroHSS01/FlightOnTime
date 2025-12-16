package com.backend.fot.dto;


/**
 * Immutable DTO representing individual field errors, which will be used in the ErrorResponse DTO.
 *
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-15
 */


public record ErrorFildsDTO(
        String field,
        String message
) {
}
