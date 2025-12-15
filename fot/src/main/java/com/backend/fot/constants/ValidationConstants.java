package com.backend.fot.constants;

/**
 * Centralized validation constants for flight-related data.
 * <p>
 * This class follows the DRY (Don't Repeat Yourself) principle by centralizing
 * all validation patterns, messages, and constraints used throughout the application.
 * </p>
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-15
 */
public final class ValidationConstants {

    // Prevent instantiation of utility class
    private ValidationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== REGEX PATTERNS ====================

    /**
     * Pattern for flight number validation.
     * Format: 2-3 uppercase letters (airline code) + 1-4 digits (flight number)
     * Examples: AA1234, BA101, LH456
     */
    public static final String FLIGHT_NUMBER_PATTERN = "^[A-Z]{2,3}\\d{1,4}$";

    /**
     * Pattern for airline IATA code validation.
     * Format: 2-3 uppercase letters
     * Examples: AA, BA, AZ, LH
     */
    public static final String AIRLINE_CODE_PATTERN = "^[A-Z]{2,3}$";

    /**
     * Pattern for airport IATA code validation.
     * Format: Exactly 3 uppercase letters
     * Examples: JFK, LAX, GRU, GIG
     */
    public static final String AIRPORT_CODE_PATTERN = "^[A-Z]{3}$";

    // ==================== SIZE CONSTRAINTS ====================

    public static final int FLIGHT_NUMBER_MIN_LENGTH = 3;
    public static final int FLIGHT_NUMBER_MAX_LENGTH = 7;

    public static final int AIRLINE_CODE_MIN_LENGTH = 2;
    public static final int AIRLINE_CODE_MAX_LENGTH = 3;

    public static final int AIRPORT_CODE_LENGTH = 3;

    // ==================== DISTANCE CONSTRAINTS ====================

    /**
     * Minimum flight distance in kilometers.
     * Based on the shortest commercial flight routes (e.g., Westray to Papa Westray: ~2.7 km)
     */
    public static final int MIN_FLIGHT_DISTANCE_KM = 1;

    /**
     * Maximum flight distance in kilometers.
     * Based on the longest commercial flight routes (e.g., Singapore to Newark: ~15,350 km)
     */
    public static final int MAX_FLIGHT_DISTANCE_KM = 20000;

    // ==================== VALIDATION MESSAGES ====================

    public static final String FLIGHT_NUMBER_REQUIRED_MSG = "Flight number is required";
    public static final String FLIGHT_NUMBER_INVALID_MSG = 
        "Flight number must be in format: 2-3 uppercase letters + 1-4 digits (e.g., AA1234)";
    public static final String FLIGHT_NUMBER_SIZE_MSG = 
        "Flight number must be between " + FLIGHT_NUMBER_MIN_LENGTH + " and " + FLIGHT_NUMBER_MAX_LENGTH + " characters";

    public static final String AIRLINE_CODE_REQUIRED_MSG = "Airline company code is required";
    public static final String AIRLINE_CODE_INVALID_MSG = "Airline code must be 2 or 3 uppercase letters";
    public static final String AIRLINE_CODE_SIZE_MSG = 
        "Airline code must be between " + AIRLINE_CODE_MIN_LENGTH + " and " + AIRLINE_CODE_MAX_LENGTH + " characters";

    public static final String ORIGIN_REQUIRED_MSG = "Origin airport code is required";
    public static final String ORIGIN_INVALID_MSG = 
        "Origin airport code must be exactly 3 uppercase letters (IATA format)";
    public static final String ORIGIN_SIZE_MSG = 
        "Origin airport code must be exactly " + AIRPORT_CODE_LENGTH + " characters";

    public static final String DESTINATION_REQUIRED_MSG = "Destination airport code is required";
    public static final String DESTINATION_INVALID_MSG = 
        "Destination airport code must be exactly 3 uppercase letters (IATA format)";
    public static final String DESTINATION_SIZE_MSG = 
        "Destination airport code must be exactly " + AIRPORT_CODE_LENGTH + " characters";

    public static final String DEPARTURE_DATE_REQUIRED_MSG = "Departure date and time is required";
    public static final String DEPARTURE_DATE_FUTURE_MSG = "Departure date must be in the present or future";

    public static final String DISTANCE_REQUIRED_MSG = "Flight distance is required";
    public static final String DISTANCE_POSITIVE_MSG = "Flight distance must be greater than 0";
    public static final String DISTANCE_RANGE_MSG = 
        "Flight distance must be between " + MIN_FLIGHT_DISTANCE_KM + " and " + MAX_FLIGHT_DISTANCE_KM + " kilometers";

    public static final String SAME_ORIGIN_DESTINATION_MSG = 
        "Origin and destination airports must be different";

    // ==================== SWAGGER DESCRIPTIONS ====================

    public static final String FLIGHT_NUMBER_DESC = 
        "Flight number in format: airline code (2-3 letters) + flight number (1-4 digits)";
    public static final String AIRLINE_CODE_DESC = "Airline IATA code (2 or 3 uppercase letters)";
    public static final String AIRPORT_ORIGIN_DESC = "Origin airport IATA code (3 uppercase letters)";
    public static final String AIRPORT_DESTINATION_DESC = "Destination airport IATA code (3 uppercase letters)";
    public static final String DEPARTURE_DATE_DESC = 
        "Scheduled departure date and time (ISO-8601 format, must be present or future)";
    public static final String DISTANCE_DESC = "Flight distance in kilometers (must be positive)";

    // ==================== JSON FORMAT PATTERNS ====================

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIMEZONE_UTC = "UTC";
}
