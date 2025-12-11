package com.backend.fot.dto;

//Java imports
import java.util.Date;

//Swagger imports
import io.swagger.v3.oas.annotations.media.Schema;

//Jakarta validation imports
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//Lombok imports
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//Swagger annotation for API documentation
@Schema(description = "Request DTO for flight delay prediction")

// Lombok annotations to reduce boilerplate code
@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightPredictionRequestDTO {

    @Schema(description = "Flight number", example = "AA1234")
    @NotBlank
    @Size(max = 6)
    private String flightNumber;

    @Schema(description = "Airline company name", example = "American Airlines / AZ")
    @NotBlank
    @Size(max = 3)
    private String companyName;

    @Schema(description = "Origin airport code", example = "JFK")
    @NotBlank
    @Size(max = 3)
    private String flightOrigin;

    @Schema(description = "Destination airport code", example = "LAX")
    @NotBlank
    @Size(max = 3)
    private String flightDestination;

    @Schema(description = "Scheduled departure date and time", example = "2025-12-11T10:00:00Z")
    @NotNull
    @FutureOrPresent
    private Date flightDepartureDate;

    @Schema(description = "Flight distance in kilometers", example = "350")
    @NotNull
    @Positive
    @Min(1)
    private int flightDistance;
}
