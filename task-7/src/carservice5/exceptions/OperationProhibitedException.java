package carservice5.exceptions;

public class OperationProhibitedException extends RuntimeException {
    public OperationProhibitedException(String message) {
        super(message);
    }
}
