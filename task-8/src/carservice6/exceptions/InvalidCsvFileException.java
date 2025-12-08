package carservice6.exceptions;

public class InvalidCsvFileException extends RuntimeException {
    public InvalidCsvFileException(String message) {
        super(message);
    }
}
