package infrastructure.factory;

import domain.core.Customer;
import domain.core.Role;
import domain.core.Technician;
import domain.core.User;

/**
 * Factory for creating User objects.
 * Factory Pattern: Centralizes user creation logic.
 *
 * Benefits:
 * - Single place to create users
 * - Easy to add new user types
 * - Follows Open/Closed Principle
 *
 * SOLID Principle: Single Responsibility - Only creates users
 */
public class UserFactory {

    /**
     * Creates a user based on role.
     *
     * @param userId Unique user ID
     * @param name User's name
     * @param pin User's PIN
     * @param role User's role (CUSTOMER or TECHNICIAN)
     * @return Customer or Technician instance
     * @throws IllegalArgumentException if role is invalid
     */
    public static User createUser(String userId, String name, String pin, Role role) {
        switch (role) {
            case CUSTOMER:
                return new Customer(userId, name, pin);

            case TECHNICIAN:
                return new Technician(userId, name, pin, userId); // Use userId as technicianId

            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    /**
     * Creates a customer.
     *
     * @param userId User ID
     * @param name Customer name
     * @param pin Customer PIN
     * @return Customer instance
     */
    public static Customer createCustomer(String userId, String name, String pin) {
        return new Customer(userId, name, pin);
    }

    /**
     * Creates a technician.
     *
     * @param userId User ID
     * @param name Technician name
     * @param pin Technician PIN
     * @param technicianId Employee/Technician ID
     * @return Technician instance
     */
    public static Technician createTechnician(String userId, String name, String pin, String technicianId) {
        return new Technician(userId, name, pin, technicianId);
    }
}