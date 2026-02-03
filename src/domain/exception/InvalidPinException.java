package domain.exception;

/**
 * Exception thrown when user authentication fails due to invalid PIN.
 *
 * SOLID Principle: Single Responsibility - Represents authentication failure
 * OOP Pillar: Encapsulation - Type-safe error representation
 */
public class InvalidPinException extends RuntimeException {
    /**
     * Creates exception with a default message.
     */
    public InvalidPinException() {
        super("Invalid PIN provided");
    }

    /**
     *with a custom message.
     *
     * @param message Custom error message
     */
    public InvalidPinException(String message) {
        super(message);
    }

    /**
     * with message and cause.
     *
     * @param message Custom error message
     * @param cause Original exception cause
     */
    public InvalidPinException(String message, Throwable cause) {
        super(message, cause);
    }
}