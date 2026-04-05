package ru.indemion.carservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.indemion.carservice.api.controllers.ErrorResponse;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        int status = HttpServletResponse.SC_FORBIDDEN;
        response.setStatus(status);
        response.setContentType("application/json");

        ErrorResponse errorResponse = new ErrorResponse(status, "Forbidden", accessDeniedException.getMessage());
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
