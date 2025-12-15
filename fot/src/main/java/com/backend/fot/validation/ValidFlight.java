package com.backend.fot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for flight prediction requests.
 * <p>
 * This annotation validates business rules that require multiple fields:
 * <ul>
 *   <li>Origin and destination must be different</li>
 *   <li>Flight distance must be realistic for the route</li>
 *   <li>Departure date must not be too far in the future</li>
 * </ul>
 * </p>
 * <p>
 * This follows the Single Responsibility Principle by separating
 * cross-field validation logic from the DTO class.
 * </p>
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-15
 * 
 * @see ValidFlightValidator
 */
@Documented
@Constraint(validatedBy = ValidFlightValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFlight {

    /**
     * Default validation error message.
     * 
     * @return error message
     */
    String message() default "Invalid flight data";

    /**
     * Validation groups.
     * 
     * @return validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload for clients to assign custom payload objects to a constraint.
     * 
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};
}
