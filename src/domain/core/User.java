package domain.core;

/**
 * Abstract base class for all users of the ATM.
 *
 * SOLID Principles:
 * - Single Responsibility: Manages common user data
 * - Open/Closed: Open for extension (Customer, Technician), closed for modification
 * - Liskov Substitution: Subclasses can be used wherever User is expected
 *
 * OOP Pillars:
 * - Abstraction: Provides abstract contract for subclasses
 * - Encapsulation: Private fields with controlled access
 * - Inheritance: Base class for Customer and Technician
 * - Polymorphism: Abstract method displayMenu() implemented differently by subclasses
 *
 * Design Pattern: Template Method (abstract methods for subclass implementation)
 */
public abstract class User {
    private String userId;
    private String name;
    private String pin;

    /**
     * User's role either the customer or technician
     */
    private Role role;

    /**
     * Constructor for creating a new user.
     */
    public User(String userId, String name, String pin, Role role) {
        this.userId = userId;
        this.name = name;
        this.pin = pin;
        this.role = role;
    }

    /**
     * Default constructor for JSON deserialization.
     */
    public User() {
    }

    // Getters and Setters (Encapsulation)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Validates user's PIN.
     *
     * @param inputPin PIN to validate
     * @return true if PIN matches, false otherwise
     */
    public boolean validatePin(String inputPin) {
        return this.pin != null && this.pin.equals(inputPin);
    }

    /**
     * Abstract method to display user-specific menu.
     * Subclasses (Customer, Technician) implement their own menu display logic.
     *
     * OOP Pillar: Polymorphism - Different implementations for different user types
     */
    public abstract void displayMenu();

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}