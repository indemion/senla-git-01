package ru.indemion.carservice.exceptions;

public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable e) {
        super(message, e);
    }
}
