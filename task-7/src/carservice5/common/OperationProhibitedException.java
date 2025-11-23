package carservice5.common;

public class OperationProhibitedException extends RuntimeException {
    public OperationProhibitedException(String message) {
        super(message);
    }
}
