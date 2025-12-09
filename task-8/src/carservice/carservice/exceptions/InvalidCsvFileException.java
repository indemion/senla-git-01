package carservice.exceptions;

public class InvalidCsvFileException extends RuntimeException {
    public InvalidCsvFileException(String message) {
        super(message);
    }
}
