package com.backend.fot.dto;

import java.time.LocalDateTime;

import com.backend.fot.constants.ValidationConstants;
import com.backend.fot.validation.ValidFlight;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Immutable DTO for flight delay prediction requests.
 * Thread-safe and validates all business rules.
 * 
 * @author FlightOnTime Team
 * @version 2.0
 */
@Schema(
    description = "Request payload for flight delay prediction API",
    example = """
        {
          "flightNumber": "AA1234",
          "companyName": "AA",
          "flightOrigin": "JFK",
          "flightDestination": "LAX",
          "flightDepartureDate": "2025-12-20T14:30:00",
          "flightDistance": 3974
        }
        """
)
@Value
@Builder(toBuilder = true)
@Jacksonized
@AllArgsConstructor
@ValidFlight
public class FlightPredictionRequestDTO {

    @Schema(description = ValidationConstants.FLIGHT_NUMBER_DESC, example = "AA1234")
    @NotBlank(message = ValidationConstants.FLIGHT_NUMBER_REQUIRED_MSG)
    @Pattern(regexp = ValidationConstants.FLIGHT_NUMBER_PATTERN, message = ValidationConstants.FLIGHT_NUMBER_INVALID_MSG)
    @Size(min = ValidationConstants.FLIGHT_NUMBER_MIN_LENGTH, max = ValidationConstants.FLIGHT_NUMBER_MAX_LENGTH, 
          message = ValidationConstants.FLIGHT_NUMBER_SIZE_MSG)
    String flightNumber;

    @Schema(description = ValidationConstants.AIRLINE_CODE_DESC, example = "AA")
    @NotBlank(message = ValidationConstants.AIRLINE_CODE_REQUIRED_MSG)
    @Pattern(regexp = ValidationConstants.AIRLINE_CODE_PATTERN, message = ValidationConstants.AIRLINE_CODE_INVALID_MSG)
    @Size(min = ValidationConstants.AIRLINE_CODE_MIN_LENGTH, max = ValidationConstants.AIRLINE_CODE_MAX_LENGTH, 
          message = ValidationConstants.AIRLINE_CODE_SIZE_MSG)
    String companyName;

    @Schema(description = ValidationConstants.AIRPORT_ORIGIN_DESC, example = "GIG")
    @NotBlank(message = ValidationConstants.ORIGIN_REQUIRED_MSG)
    @Pattern(regexp = ValidationConstants.AIRPORT_CODE_PATTERN, message = ValidationConstants.ORIGIN_INVALID_MSG)
    @Size(min = ValidationConstants.AIRPORT_CODE_LENGTH, max = ValidationConstants.AIRPORT_CODE_LENGTH, 
          message = ValidationConstants.ORIGIN_SIZE_MSG)
    String flightOrigin;

    @Schema(description = ValidationConstants.AIRPORT_DESTINATION_DESC, example = "GRU")
    @NotBlank(message = ValidationConstants.DESTINATION_REQUIRED_MSG)
    @Pattern(regexp = ValidationConstants.AIRPORT_CODE_PATTERN, message = ValidationConstants.DESTINATION_INVALID_MSG)
    @Size(min = ValidationConstants.AIRPORT_CODE_LENGTH, max = ValidationConstants.AIRPORT_CODE_LENGTH, 
          message = ValidationConstants.DESTINATION_SIZE_MSG)
    String flightDestination;

    @Schema(description = ValidationConstants.DEPARTURE_DATE_DESC, example = "2025-12-20T14:30:00", 
            type = "string", format = "date-time")
    @NotNull(message = ValidationConstants.DEPARTURE_DATE_REQUIRED_MSG)
    @FutureOrPresent(message = ValidationConstants.DEPARTURE_DATE_FUTURE_MSG)
    @JsonFormat(pattern = ValidationConstants.DATE_TIME_PATTERN, timezone = ValidationConstants.TIMEZONE_UTC)
    LocalDateTime flightDepartureDate;

    @Schema(description = ValidationConstants.DISTANCE_DESC, example = "3974", 
            minimum = "1", maximum = "20000", type = "integer")
    @NotNull(message = ValidationConstants.DISTANCE_REQUIRED_MSG)
    @Positive(message = ValidationConstants.DISTANCE_POSITIVE_MSG)
    Integer flightDistance;

    /**
     * Returns a new DTO with all string fields normalized to uppercase.
     */
    public FlightPredictionRequestDTO toUpperCase() {
        return this.toBuilder()
                   .flightNumber(flightNumber != null ? flightNumber.toUpperCase() : null)
                   .companyName(companyName != null ? companyName.toUpperCase() : null)
                   .flightOrigin(flightOrigin != null ? flightOrigin.toUpperCase() : null)
                   .flightDestination(flightDestination != null ? flightDestination.toUpperCase() : null)
                   .build();
    }

    /**
     * Checks if this is likely a domestic flight based on IATA code first letter.
     * Note: Simplified check - production should use proper airport database.
     */
    public boolean isDomesticFlight() {
        if (flightOrigin == null || flightDestination == null) {
            return false;
        }
        return flightOrigin.charAt(0) == flightDestination.charAt(0);
    }

    /**
     * Returns the flight category based on distance.
     */
    public FlightCategory getFlightCategory() {
        if (flightDistance == null) {
            return FlightCategory.UNKNOWN;
        }
        if (flightDistance < 1500) {
            return FlightCategory.SHORT_HAUL;
        } else if (flightDistance < 3500) {
            return FlightCategory.MEDIUM_HAUL;
        } else if (flightDistance < 10000) {
            return FlightCategory.LONG_HAUL;
        } else {
            return FlightCategory.ULTRA_LONG_HAUL;
        }
    }

    public enum FlightCategory {
        SHORT_HAUL,      // < 1,500 km
        MEDIUM_HAUL,     // 1,500 - 3,500 km
        LONG_HAUL,       // 3,500 - 10,000 km
        ULTRA_LONG_HAUL, // > 10,000 km
        UNKNOWN
    }
}
