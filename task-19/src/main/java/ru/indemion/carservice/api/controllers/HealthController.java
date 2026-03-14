package ru.indemion.carservice.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.indemion.carservice.dto.HealthResponseDto;
import ru.indemion.carservice.models.services.HealthService;

@RestController
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponseDto> healthCheck() {
        HealthResponseDto responseDto = healthService.healthCheck();
        return ResponseEntity.status(healthService.getHttpStatus(responseDto)).body(responseDto);
    }
}
