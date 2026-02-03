package infrastructure.persistence.interfaces;

import domain.core.Account;
import java.util.List;

/**
 * Repository interface for Account data access.
 *
 * SOLID Principles:
 * - Dependency Inversion: TransactionService depends on this interface
 * - Single Responsibility: Only account persistence operations
 */
public interface AccountRepository {

    /**
     * Finds account by ID.
     *
     * @param accountId Account ID
     * @return Account object or null if not found
     */
    Account findById(String accountId);

    /**
     * Finds all accounts belonging to a user.
     *
     * @param userId User's ID
     * @return List of accounts
     */
    List<Account> findByUserId(String userId);

    /**
     * Gets all accounts.
     *
     * @return List of all accounts
     */
    List<Account> findAll();

    /**
     * Saves a new account.
     *
     * @param account Account to save
     */
    void save(Account account);

    /**
     * Updates existing account.
     * Used after transactions to update balance.
     * Bank persists balance changes.
     *
     * @param account Account with updated data
     */
    void update(Account account);

    /**
     * Deletes an account.
     *
     * @param accountId Account ID to delete
     */
    void delete(String accountId);
}