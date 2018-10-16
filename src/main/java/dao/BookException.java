package dao;

/**
 * Exception thrown when a book mapper detects a problem
 * during a persistence operation.
 */
public class BookException extends AppException {
    public BookException(String message) {
        super(message);
    }
}

