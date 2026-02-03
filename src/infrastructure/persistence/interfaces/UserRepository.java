package infrastructure.persistence.interfaces;

import domain.core.User;
import java.util.List;

/**
 * Repository interface for User data access.
 * Abstracts how users are stored like JSON
 * SOLID Principles:
 * - Dependency Inversion: Services depend on this interface, not implementations
 * - Interface Segregation: Only user-related operations
 */
public interface UserRepository {

    /**
     * Finds user by name.
     * Used for authentication.
     *
     * @param name User's name
     * @return User object or null if not found
     */
    User findByName(String name);

    /**
     * Finds user by ID.
     *
     * @param userId User's ID
     * @return User object or null if not found
     */
    User findById(String userId);

    /**
     * Gets all users.
     *
     * @return List of all users
     */
    List<User> findAll();

    /**
     * Saves a new user or updates existing user.
     *
     * @param user User to save
     */
    void save(User user);

    /**
     * Deletes a user.
     *
     * @param userId User ID to delete
     */
    void delete(String userId);
}
