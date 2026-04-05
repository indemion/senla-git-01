package ru.indemion.carservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.indemion.carservice.api.controllers.ErrorResponse;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        response.setStatus(status);
        response.setContentType("application/json");

        ErrorResponse errorResponse = new ErrorResponse(status, "Unauthorized", authException.getMessage());
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
