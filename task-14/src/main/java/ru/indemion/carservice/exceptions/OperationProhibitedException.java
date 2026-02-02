package ru.indemion.carservice.exceptions;

public class OperationProhibitedException extends RuntimeException {
    public OperationProhibitedException(String message) {
        super(message);
    }
}
