package domain.exception;

/**
 * Exception thrown when ATM lacks necessary resources like cash, ink, paper
 *
 * SOLID Principle: Single Responsibility - Represents resource shortage
 * OOP Pillar: Encapsulation - Type-safe error representation
 */
public class ATMResourceException extends RuntimeException {
    /**
     * Creates exception with a default message.
     */
    public ATMResourceException() {
        super("ATM resource unavailable");
    }

    /**
     * exception with a custom message.
     *
     * @param message Custom error message
     */
    public ATMResourceException(String message) {
        super(message);
    }

    /**
     * with message and cause.
     *
     * @param message Custom error message
     * @param cause Original exception cause
     */
    public ATMResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}