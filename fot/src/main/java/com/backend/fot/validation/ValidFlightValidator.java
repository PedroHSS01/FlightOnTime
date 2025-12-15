package com.backend.fot.validation;

import com.backend.fot.dto.FlightPredictionRequestDTO;
import com.backend.fot.constants.ValidationConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Validator implementation for {@link ValidFlight} annotation.
 * <p>
 * This class implements cross-field business validations that cannot be
 * handled by simple field-level annotations. It follows the Single Responsibility
 * Principle by focusing solely on flight-specific business rule validation.
 * </p>
 * <p>
 * Validations performed:
 * <ul>
 *   <li>Origin ≠ Destination (different airports)</li>
 *   <li>Flight distance is realistic (between 1 and 20,000 km)</li>
 *   <li>Departure date is not more than 1 year in the future</li>
 * </ul>
 * </p>
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-15
 */
public class ValidFlightValidator implements ConstraintValidator<ValidFlight, FlightPredictionRequestDTO> {

    /**
     * Maximum days in the future allowed for departure date.
     * Commercial flights are typically bookable up to 330-365 days in advance.
     */
    private static final long MAX_DAYS_IN_FUTURE = 365;

    @Override
    public void initialize(ValidFlight constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(FlightPredictionRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Let @NotNull handle null validation
        }

        // Disable default constraint violation
        context.disableDefaultConstraintViolation();

        boolean isValid = true;

        // Validation 1: Origin and destination must be different
        isValid &= validateDifferentAirports(dto, context);

        // Validation 2: Flight distance must be within realistic range
        isValid &= validateFlightDistance(dto, context);

        // Validation 3: Departure date must not be too far in the future
        isValid &= validateDepartureDate(dto, context);

        return isValid;
    }

    /**
     * Validates that origin and destination airports are different.
     * 
     * @param dto the flight request DTO
     * @param context the validation context
     * @return true if airports are different, false otherwise
     */
    private boolean validateDifferentAirports(FlightPredictionRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getFlightOrigin() == null || dto.getFlightDestination() == null) {
            return true; // Let field-level validations handle null
        }

        if (dto.getFlightOrigin().equalsIgnoreCase(dto.getFlightDestination())) {
            context.buildConstraintViolationWithTemplate(ValidationConstants.SAME_ORIGIN_DESTINATION_MSG)
                   .addPropertyNode("flightDestination")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * Validates that flight distance is within realistic commercial flight ranges.
     * <p>
     * Rationale:
     * - Minimum: Shortest commercial flights are around 2-3 km
     * - Maximum: Longest commercial flights are around 15,350 km
     * - We use 1-20,000 km to allow some margin
     * </p>
     * 
     * @param dto the flight request DTO
     * @param context the validation context
     * @return true if distance is realistic, false otherwise
     */
    private boolean validateFlightDistance(FlightPredictionRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getFlightDistance() == null) {
            return true; // Let field-level validations handle null
        }

        int distance = dto.getFlightDistance();

        if (distance < ValidationConstants.MIN_FLIGHT_DISTANCE_KM || 
            distance > ValidationConstants.MAX_FLIGHT_DISTANCE_KM) {
            context.buildConstraintViolationWithTemplate(ValidationConstants.DISTANCE_RANGE_MSG)
                   .addPropertyNode("flightDistance")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * Validates that departure date is not too far in the future.
     * <p>
     * Commercial flights are typically bookable up to 330-365 days in advance.
     * This validation prevents unrealistic future dates.
     * </p>
     * 
     * @param dto the flight request DTO
     * @param context the validation context
     * @return true if date is within acceptable range, false otherwise
     */
    private boolean validateDepartureDate(FlightPredictionRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getFlightDepartureDate() == null) {
            return true; // Let field-level validations handle null
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureDate = dto.getFlightDepartureDate();
        long daysBetween = ChronoUnit.DAYS.between(now, departureDate);

        if (daysBetween > MAX_DAYS_IN_FUTURE) {
            context.buildConstraintViolationWithTemplate(
                       "Departure date cannot be more than " + MAX_DAYS_IN_FUTURE + " days in the future")
                   .addPropertyNode("flightDepartureDate")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
