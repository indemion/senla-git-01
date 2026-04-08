package ru.indemion.carservice.api.controllers.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.indemion.carservice.common.HealthStatus;
import ru.indemion.carservice.dto.HealthResponseDto;
import ru.indemion.carservice.models.services.HealthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {
    @Mock
    private HealthService healthService;

    @InjectMocks
    private HealthController healthController;

    @Test
    void healthCheck_shouldReturnOkStatusAndBody() {
        // ARRANGE
        HealthResponseDto mockResponse = new HealthResponseDto(HealthStatus.UP, HealthStatus.UP);
        when(healthService.healthCheck()).thenReturn(mockResponse);
        when(healthService.getHttpStatus(mockResponse)).thenReturn(HttpStatus.OK);

        // ACT
        ResponseEntity<HealthResponseDto> response = healthController.healthCheck();

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(healthService).healthCheck();
        verify(healthService).getHttpStatus(mockResponse);
    }

    @Test
    void healthCheck_shouldReturnUnavailableAndBody() {
        // ARRANGE
        HealthResponseDto mockResponse = new HealthResponseDto(HealthStatus.UP, HealthStatus.DOWN);
        when(healthService.healthCheck()).thenReturn(mockResponse);
        when(healthService.getHttpStatus(mockResponse)).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

        // ACT
        ResponseEntity<HealthResponseDto> response = healthController.healthCheck();

        // ASSERT
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(healthService).healthCheck();
        verify(healthService).getHttpStatus(mockResponse);
    }
}