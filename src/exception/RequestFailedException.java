package exception;

import java.io.IOException;

public class RequestFailedException extends RuntimeException {

    public RequestFailedException() {
    }
    public RequestFailedException(String message) {
        super(message);
    }

    public RequestFailedException(String s, IOException exception) {
        super(s, exception);
    }
}
