package domain.core;

/**
 * Enum representing user roles in the ATM system.
 *
 * SOLID Principle: Single Responsibility - Only defines user role types
 * OOP Pillar: Encapsulation - Type-safe enumeration prevents invalid roles
 */
public enum Role {
    CUSTOMER,
    TECHNICIAN
}