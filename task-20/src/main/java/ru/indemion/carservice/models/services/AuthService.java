package ru.indemion.carservice.models.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.indemion.carservice.dto.GenerateTokenDto;
import ru.indemion.carservice.dto.JwtResponseDto;
import ru.indemion.carservice.security.JwtTokenProvider;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public JwtResponseDto generateToken(GenerateTokenDto generateTokenDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(generateTokenDto.getUsername(),
                            generateTokenDto.getPassword()));

            return new JwtResponseDto(jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Неправильный username или password", ex);
        }
    }
}
