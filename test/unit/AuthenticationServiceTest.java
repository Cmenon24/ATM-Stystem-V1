package unit;

import application.service.AuthenticationService;
import domain.core.Customer;
import domain.core.Role;
import domain.core.Technician;
import domain.core.User;
import domain.exception.InvalidPinException;
import infrastructure.factory.UserFactory;
import infrastructure.persistence.interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for AuthenticationService.
 * Tests authentication logic in isolation using a mock repository.
 */
class AuthenticationServiceTest {

    private AuthenticationService authService;
    private MockUserRepository mockUserRepository;

    @BeforeEach
    void setUp() {
        // Create mock repository with test users
        mockUserRepository = new MockUserRepository();

        // Add test users
        Customer john = (Customer) UserFactory.createUser("USER001", "John", "1234", Role.CUSTOMER);
        Technician jake = (Technician) UserFactory.createUser("USER002", "Jake", "9999", Role.TECHNICIAN);

        mockUserRepository.save(john);
        mockUserRepository.save(jake);

        // Create service with mock repository
        authService = new AuthenticationService(mockUserRepository);
    }

    @Test
    @DisplayName("Should authenticate customer with correct credentials")
    void testAuthenticateCustomerSuccess() {
        // Act
        User user = authService.authenticate("John", "1234");

        // Assert
        assertNotNull(user);
        assertEquals("John", user.getName());
        assertEquals(Role.CUSTOMER, user.getRole());
        assertTrue(user instanceof Customer);
    }

    @Test
    @DisplayName("Should authenticate technician with correct credentials")
    void testAuthenticateTechnicianSuccess() {
        // Act
        User user = authService.authenticate("Jake", "9999");

        // Assert
        assertNotNull(user);
        assertEquals("Jake", user.getName());
        assertEquals(Role.TECHNICIAN, user.getRole());
        assertTrue(user instanceof Technician);
    }

    @Test
    @DisplayName("Should throw InvalidPinException for incorrect PIN")
    void testAuthenticateWrongPin() {
        // Act & Assert
        InvalidPinException exception = assertThrows(
                InvalidPinException.class,
                () -> authService.authenticate("John", "wrong")
        );

        assertEquals("Invalid credentials. Please try again.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidPinException for non-existent user")
    void testAuthenticateNonExistentUser() {
        // Act & Assert
        InvalidPinException exception = assertThrows(
                InvalidPinException.class,
                () -> authService.authenticate("NonExistent", "1234")
        );

        assertEquals("Invalid credentials. Please try again.", exception.getMessage());
    }

    /**
     * Mock implementation of UserRepository for testing.
     * Simulates in-memory storage without JSON files.
     */
    private static class MockUserRepository implements UserRepository {
        private final List<User> users = new ArrayList<>();

        @Override
        public User findByName(String name) {
            return users.stream()
                    .filter(u -> u.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public User findById(String userId) {
            return users.stream()
                    .filter(u -> u.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(users);
        }

        @Override
        public void save(User user) {
            users.removeIf(u -> u.getUserId().equals(user.getUserId()));
            users.add(user);
        }

        @Override
        public void delete(String userId) {
            users.removeIf(u -> u.getUserId().equals(userId));
        }
    }
}
