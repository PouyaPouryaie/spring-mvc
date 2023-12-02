package ir.bigz.springboot.springmvc.exceptions;

import java.io.Serial;

public class PostDeletionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PostDeletionException() {
        this("Post can't be deleted");
    }

    public PostDeletionException(String message) {
        this(message, null);
    }

    public PostDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
