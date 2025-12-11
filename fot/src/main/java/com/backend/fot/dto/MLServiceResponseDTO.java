package com.backend.fot.dto;

//Swagger imports
import io.swagger.v3.oas.annotations.media.Schema;

//Lombok imports
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response DTO from ML service for flight predictions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLServiceResponseDTO {
    
    @Schema(description = "Prediction result from ML model ( 0 or 1 )", example = "1")
    private Integer prediction;
    
    @Schema(description = "Confidence score from ML model", example = "0.92")
    private Double confidence;
    
}
