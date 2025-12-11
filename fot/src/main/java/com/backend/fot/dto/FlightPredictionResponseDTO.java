package com.backend.fot.dto;

//Custom imports
import com.backend.fot.enums.FlightPrediction;

//Swagger imports
import io.swagger.v3.oas.annotations.media.Schema;

//Lombok imports
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response DTO containing flight delay prediction results")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightPredictionResponseDTO {

    @Schema(description = "Predicted outcome for the flight (ON_TIME or DELAYED)", example = "DELAYED")
    private FlightPrediction prediction;
    
    @Schema(description = "Probability/confidence score of the prediction", example = "0.85")
    private Double probability;

}
