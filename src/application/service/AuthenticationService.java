package application.service;

import domain.core.User;
import domain.exception.InvalidPinException;
import infrastructure.persistence.interfaces.UserRepository;

/**SOLID Principle: Single Responsibility - Only handles authentication for customers and technicians*/

public class AuthenticationService {

    private final UserRepository userRepository;

    /**
     * Constructor with dependency injection.
     *
     * @param userRepository Repository for user data access
     */
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates user by name and PIN.
     *
     * Process:
     * 1. Look up user by name in repository (Bank database for customers)
     * 2. Validate PIN matches
     * 3. Return authenticated user or throw exception
     *
     * @return Authenticated User object (Customer or Technician)
     * @throws InvalidPinException if name not found or PIN incorrect
     */
    public User authenticate(String name, String pin) {
        // Retrieve user from repository (Bank provides customer data)
        User user = userRepository.findByName(name);

        // Check if user exists and PIN is correct
        if (user == null || !user.validatePin(pin)) {
            throw new InvalidPinException("Invalid credentials. Please try again.");
        }

        // Return authenticated user
        return user;
    }
}