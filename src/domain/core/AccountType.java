package domain.core;

/**
 * Enum representing types of set named bank accounts.
 *
 * SOLID Principle: Single Responsibility - Only defines account types
 * OOP Pillar: Encapsulation - Type-safe enumeration ensures valid account types
 */
public enum AccountType {
    CHECKING,
    SAVINGS
}