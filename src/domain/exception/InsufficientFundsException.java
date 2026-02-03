package domain.exception;

/**
 * Exception thrown when an account has insufficient funds for a transaction.
 *
 * SOLID Principle: Single Responsibility - Represents one specific error condition
 * OOP Pillar: Encapsulation - Wraps error information in a type-safe manner
 */
public class InsufficientFundsException extends RuntimeException {
    /**
     * Creates exception with a default message.
     */
    public InsufficientFundsException() {
        super("Insufficient funds in account");
    }

    /**
     * with a custom message.
     *
     * @param message Custom error message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * with message and cause.
     *
     * @param message Custom error message
     * @param cause Original exception cause
     */
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}