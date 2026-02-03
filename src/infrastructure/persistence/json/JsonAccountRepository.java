package infrastructure.persistence.json;

import com.google.gson.reflect.TypeToken;
import domain.core.Account;
import infrastructure.persistence.interfaces.AccountRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON implementation of AccountRepository.
 * Stores accounts in accounts.json file.
 *
 * File: data/accounts.json
 * Format: JSON array of Account objects
 */
public class JsonAccountRepository implements AccountRepository {

    private static final String FILENAME = "accounts.json";
    private final JsonFileHandler fileHandler;

    public JsonAccountRepository(String dataDirectory) {
        this.fileHandler = new JsonFileHandler(dataDirectory);
    }

    /**
     * Loads all accounts from JSON file.
     */
    private List<Account> loadAccounts() {
        Type listType = new TypeToken<ArrayList<Account>>(){}.getType();
        List<Account> accounts = fileHandler.readFromFile(FILENAME, listType);
        return accounts != null ? accounts : new ArrayList<>();
    }

    /**
     * Saves all accounts to JSON file.
     */
    private void saveAccounts(List<Account> accounts) {
        fileHandler.writeToFile(FILENAME, accounts);
    }

    @Override
    public Account findById(String accountId) {
        List<Account> accounts = loadAccounts();
        return accounts.stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Account> findByUserId(String userId) {
        List<Account> accounts = loadAccounts();
        return accounts.stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findAll() {
        return loadAccounts();
    }

    @Override
    public void save(Account account) {
        List<Account> accounts = loadAccounts();

        // Remove existing account with same ID if exists
        accounts.removeIf(a -> a.getAccountId().equals(account.getAccountId()));

        // Add account
        accounts.add(account);

        // Save to file (Bank persists)
        saveAccounts(accounts);
    }

    @Override
    public void update(Account account) {
        // Update is same as save in this implementation
        save(account);
    }

    @Override
    public void delete(String accountId) {
        List<Account> accounts = loadAccounts();
        accounts.removeIf(a -> a.getAccountId().equals(accountId));
        saveAccounts(accounts);
    }
}