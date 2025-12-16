package com.backend.fot.exceptions;

import com.backend.fot.dto.ErrorFildsDTO;
import com.backend.fot.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;


/**
 * Global exception handler for the application.
 * <p>
 * This class centralizes the handling of exceptions thrown across the application,
 * ensuring that clients receive consistent and well-structured error responses.
 * </p>
 *
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-15
 */


@ControllerAdvice
public class GlobalExceptionHendler {


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleAllExceptions(Exception e) {

        List<ErrorFildsDTO> errorFilds = List.of(new ErrorFildsDTO(e.getCause().getMessage(), e.getMessage()));

        return ErrorResponseDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                errorFilds
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleConstraintViolation(ConstraintViolationException e) {

        List<ErrorFildsDTO> errorFilds = e.getConstraintViolations()
                .stream()
                .map(violation -> new ErrorFildsDTO(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return ErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Violation Constraint Error",
                "Validation failed in constraints",
                errorFilds
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleValidationExceptions(MethodArgumentNotValidException e) {

        List<ErrorFildsDTO> errorFilds = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(violation -> new ErrorFildsDTO(
                        violation.getField(),
                        violation.getDefaultMessage()
                ))
                .toList();


        return ErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Violation Argument Error",
                "Validation failed for one or more fields",
                errorFilds
        );
    }

}
