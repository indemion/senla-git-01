package ru.indemion.carservice.api.controllers.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.indemion.carservice.dto.GenerateTokenDto;
import ru.indemion.carservice.dto.JwtResponseDto;
import ru.indemion.carservice.models.services.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void generateToken_shouldCallServiceAndReturnOkResponse() {
        // Arrange
        GenerateTokenDto requestDto = new GenerateTokenDto("user", "pass");
        JwtResponseDto serviceResponse = new JwtResponseDto("jwt-token-123");
        when(authService.generateToken(requestDto)).thenReturn(serviceResponse);

        // Act
        ResponseEntity<JwtResponseDto> response = authController.generateToken(requestDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());
        verify(authService).generateToken(requestDto);
        verifyNoMoreInteractions(authService);
    }

    @Test
    void generateToken_shouldPropagateExceptionFromService() {
        // Arrange
        GenerateTokenDto requestDto = new GenerateTokenDto("bad", "credentials");
        RuntimeException expectedException = new RuntimeException("Invalid credentials");
        when(authService.generateToken(requestDto)).thenThrow(expectedException);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> authController.generateToken(requestDto));
        assertSame(expectedException, thrown);
        verify(authService).generateToken(requestDto);
    }
}