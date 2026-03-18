package ru.indemion.carservice.api.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.indemion.carservice.dto.GenerateTokenDto;
import ru.indemion.carservice.dto.JwtResponseDto;
import ru.indemion.carservice.models.services.AuthService;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/token")
    public ResponseEntity<JwtResponseDto> generateToken(@RequestBody GenerateTokenDto generateTokenDto) {
        return ResponseEntity.ok(authService.generateToken(generateTokenDto));
    }
}
