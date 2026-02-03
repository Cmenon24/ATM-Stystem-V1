package domain.exception;

/**
 * Exception thrown when a user attempts an unauthorized operation.
 * Used mostly for Version 1 technician restrictions.
 *
 * SOLID Principle: Single Responsibility - Represents authorization failure
 * OOP Pillar: Encapsulation - Type-safe error representation
 *
 * Like when the Technician in Version 1 trying to replenish cash (requires Version 2)
 */
public class UnauthorizedOperationException extends RuntimeException {
    /**
     * Creates exception with a default message.
     */
    public UnauthorizedOperationException() {
        super("Operation not authorized");
    }

    /**
     * with a custom message.
     * @param message Custom error message
     */
    public UnauthorizedOperationException(String message) {
        super(message);
    }

    /**
     * message and cause.
     * @param message Custom error message
     * @param cause Original exception cause
     */
    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}