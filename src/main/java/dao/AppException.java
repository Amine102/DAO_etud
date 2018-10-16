package dao;

/**
 * Generic exception thrown when an abstract mapper detects a problem during a
 * persistence operation.
 */
public class AppException extends Exception {

    public AppException(String message) {
        super(message);
    }
}
