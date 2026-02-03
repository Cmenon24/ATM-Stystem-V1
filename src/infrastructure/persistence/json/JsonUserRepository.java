package infrastructure.persistence.json;

import com.google.gson.reflect.TypeToken;
import domain.core.User;
import infrastructure.persistence.interfaces.UserRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON implementation of UserRepository.
 * Stores users in users.json file.
 *
 * File: data/users.json
 * Format: JSON array of User objects
 */
public class JsonUserRepository implements UserRepository {

    private static final String FILENAME = "users.json";
    private final JsonFileHandler fileHandler;

    /**
     * Constructor.
     *
     * @param dataDirectory Directory for JSON files (e.g., "data/")
     */
    public JsonUserRepository(String dataDirectory) {
        this.fileHandler = new JsonFileHandler(dataDirectory);
    }

    /**
     * Loads all users from JSON file.
     *
     * @return List of users, or empty list if file doesn't exist
     */
    private List<User> loadUsers() {
        Type listType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> users = fileHandler.readFromFile(FILENAME, listType);
        return users != null ? users : new ArrayList<>();
    }

    /**
     * Saves all users to JSON file.
     *
     * @param users List of users to save
     */
    private void saveUsers(List<User> users) {
        fileHandler.writeToFile(FILENAME, users);
    }

    @Override
    public User findByName(String name) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findById(String userId) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return loadUsers();
    }

    @Override
    public void save(User user) {
        List<User> users = loadUsers();

        // Remove existing user with same ID if exists
        users.removeIf(u -> u.getUserId().equals(user.getUserId()));

        // Add user
        users.add(user);

        // Save to file
        saveUsers(users);
    }

    @Override
    public void delete(String userId) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getUserId().equals(userId));
        saveUsers(users);
    }
}