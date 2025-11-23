package carservice5.common;

public class InvalidCsvFileException extends RuntimeException {
    public InvalidCsvFileException(String message) {
        super(message);
    }
}
