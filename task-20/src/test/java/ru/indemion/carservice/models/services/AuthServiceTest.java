package ru.indemion.carservice.models.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import ru.indemion.carservice.dto.GenerateTokenDto;
import ru.indemion.carservice.dto.JwtResponseDto;
import ru.indemion.carservice.security.JwtTokenProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    // ==================== Позитивный сценарий ====================
    @Test
    void generateToken_shouldReturnJwtResponseDtoWhenCredentialsValid() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        GenerateTokenDto dto = new GenerateTokenDto(username, password);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        String expectedToken = "jwt-token-123";
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn(expectedToken);

        // Act
        JwtResponseDto result = authService.generateToken(dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());
        verify(authenticationManager).authenticate(
                argThat(token -> token.getPrincipal().equals(username) && token.getCredentials().equals(password))
        );
        verify(jwtTokenProvider).generateToken(userDetails);
    }

    // ==================== Негативный сценарий: неверные учётные данные ====================
    @Test
    void generateToken_shouldThrowBadCredentialsExceptionWhenAuthenticationFails() {
        // Arrange
        String username = "wronguser";
        String password = "wrongpass";
        GenerateTokenDto dto = new GenerateTokenDto(username, password);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.generateToken(dto));
        assertEquals("Неправильный username или password", ex.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any());
    }
}