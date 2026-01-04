package com.backend.fot.service;

import com.backend.fot.client.MLServiceClient;
import com.backend.fot.dto.FlightPredictionRequestDTO;
import com.backend.fot.dto.FlightPredictionResponseDTO;
import com.backend.fot.dto.MLServiceResponseDTO;
import com.backend.fot.enums.FlightPrediction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PredictionServiceImpl.
 * 
 * Boas práticas aplicadas:
 * - AAA pattern (Arrange, Act, Assert)
 * - Isolamento com mocks (Mockito)
 * - Testes parametrizados para múltiplos cenários
 * - Nomenclatura descritiva (Given-When-Then)
 * - Agrupamento lógico com @Nested
 * - Cobertura de casos de sucesso e falha
 * 
 * @author FlightOnTime Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PredictionServiceImpl Unit Tests")
class PredictionServiceImplTest {

    @Mock
    private MLServiceClient mlServiceClient;

    @InjectMocks
    private PredictionServiceImpl predictionService;

    private FlightPredictionRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        // Arrange: criar request válido para reutilização
        validRequest = FlightPredictionRequestDTO.builder()
                .flightNumber("AZ1234")
                .companyName("AZ")
                .flightOrigin("GIG")
                .flightDestination("GRU")
                .flightDepartureDate(LocalDateTime.now().plusDays(1))
                .flightDistance(350)
                .build();
    }

    @Nested
    @DisplayName("Successful Prediction Tests")
    class SuccessfulPredictionTests {

        @Test
        @DisplayName("Should return ON_TIME prediction when ML service predicts 0")
        void shouldReturnOnTimePrediction_WhenMLServicePredictsZero() {
            // Arrange
            MLServiceResponseDTO mlResponse = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.85"))
                    .build();
            
            when(mlServiceClient.predict(any(FlightPredictionRequestDTO.class)))
                    .thenReturn(mlResponse);

            // Act
            FlightPredictionResponseDTO result = predictionService.predictDelay(validRequest);

            // Assert
            assertNotNull(result, "Response should not be null");
            assertEquals(FlightPrediction.ON_TIME, result.getPrediction(), 
                    "Prediction should be ON_TIME for ML prediction 0");
            assertEquals(0.85, result.getProbability(), 0.001, 
                    "Probability should match ML service confidence");
            
            verify(mlServiceClient, times(1)).predict(validRequest);
        }

        @Test
        @DisplayName("Should return DELAYED prediction when ML service predicts 1")
        void shouldReturnDelayedPrediction_WhenMLServicePredictsOne() {
            // Arrange
            MLServiceResponseDTO mlResponse = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.92"))
                    .build();
            
            when(mlServiceClient.predict(any(FlightPredictionRequestDTO.class)))
                    .thenReturn(mlResponse);

            // Act
            FlightPredictionResponseDTO result = predictionService.predictDelay(validRequest);

            // Assert
            assertNotNull(result);
            assertEquals(FlightPrediction.DELAYED, result.getPrediction(),
                    "Prediction should be DELAYED for ML prediction 1");
            assertEquals(0.92, result.getProbability(), 0.001);
            
            verify(mlServiceClient).predict(validRequest);
        }
    }

    @Nested
    @DisplayName("Confidence Level Tests")
    class ConfidenceLevelTests {

        @ParameterizedTest(name = "Probability {0} should return confidence level {1}")
        @CsvSource({
            "0.95, VERY_HIGH",
            "0.90, VERY_HIGH",
            "0.80, HIGH",
            "0.75, HIGH",
            "0.65, MEDIUM",
            "0.60, MEDIUM",
            "0.50, LOW",
            "0.45, LOW",
            "0.30, VERY_LOW",
            "0.10, VERY_LOW"
        })
        @DisplayName("Should correctly determine confidence level based on probability")
        void shouldDetermineCorrectConfidenceLevel(String probability, String expectedLevel) {
            // Arrange
            MLServiceResponseDTO mlResponse = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal(probability))
                    .build();
            
            when(mlServiceClient.predict(any(FlightPredictionRequestDTO.class)))
                    .thenReturn(mlResponse);

            // Act
            FlightPredictionResponseDTO result = predictionService.predictDelay(validRequest);

            // Assert
            assertNotNull(result.getConfidence());
            assertEquals(
                    FlightPredictionResponseDTO.ConfidenceLevel.valueOf(expectedLevel),
                    result.getConfidence(),
                    String.format("Probability %s should map to confidence level %s", probability, expectedLevel)
            );
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw RuntimeException when ML service fails")
        void shouldThrowRuntimeException_WhenMLServiceFails() {
            // Arrange
            when(mlServiceClient.predict(any(FlightPredictionRequestDTO.class)))
                    .thenThrow(new MLServiceClient.MLServiceException("ML service unavailable"));

            // Act & Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> predictionService.predictDelay(validRequest),
                    "Should throw RuntimeException when ML service fails"
            );
            
            assertTrue(exception.getMessage().contains("Failed to get prediction"),
                    "Exception message should indicate prediction failure");
            assertInstanceOf(MLServiceClient.MLServiceException.class, exception.getCause(),
                    "Cause should be MLServiceException");
            
            verify(mlServiceClient).predict(validRequest);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle boundary probability values correctly")
        void shouldHandleBoundaryProbabilityValues() {
            // Arrange - probability at exact boundary (0.75)
            MLServiceResponseDTO mlResponse = MLServiceResponseDTO.builder()
                    .prediction(1)
                    .confidence(new BigDecimal("0.75"))
                    .build();
            
            when(mlServiceClient.predict(any())).thenReturn(mlResponse);

            // Act
            FlightPredictionResponseDTO result = predictionService.predictDelay(validRequest);

            // Assert
            assertEquals(FlightPredictionResponseDTO.ConfidenceLevel.HIGH, result.getConfidence(),
                    "Probability 0.75 should be classified as HIGH confidence");
        }

        @Test
        @DisplayName("Should correctly pass flight data to ML service")
        void shouldCorrectlyPassFlightDataToMLService() {
            // Arrange
            MLServiceResponseDTO mlResponse = MLServiceResponseDTO.builder()
                    .prediction(0)
                    .confidence(new BigDecimal("0.70"))
                    .build();
            
            when(mlServiceClient.predict(validRequest)).thenReturn(mlResponse);

            // Act
            predictionService.predictDelay(validRequest);

            // Assert - verify exact request was passed
            verify(mlServiceClient).predict(argThat(request -> 
                    request.getFlightNumber().equals("AZ1234") &&
                    request.getCompanyName().equals("AZ") &&
                    request.getFlightOrigin().equals("GIG") &&
                    request.getFlightDestination().equals("GRU") &&
                    request.getFlightDistance().equals(350)
            ));
        }
    }
}
