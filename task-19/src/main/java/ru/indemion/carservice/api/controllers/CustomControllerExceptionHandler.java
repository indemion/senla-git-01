package ru.indemion.carservice.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.indemion.carservice.exceptions.EntityNotFoundException;

@RestControllerAdvice
public class CustomControllerExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                "Ресурс не найден",
                ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                "Неправильный username или password",
                ex.getMessage());
        return new ResponseEntity<>(error, status);
    }
}
