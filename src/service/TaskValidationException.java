package service;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException() {}

    public TaskValidationException(String message) {
        super(message);
    }
}
