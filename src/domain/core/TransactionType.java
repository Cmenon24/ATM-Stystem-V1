package domain.core;

/**
 * SOLID Principle: Open/Closed - Easy to extend with new transaction types
 * OOP Pillar: Encapsulation - Type-safe enumeration prevents invalid transaction types
 */
public enum TransactionType {
    WITHDRAWAL,
    DEPOSIT,
    TRANSFER,
    BALANCE_INQUIRY
}