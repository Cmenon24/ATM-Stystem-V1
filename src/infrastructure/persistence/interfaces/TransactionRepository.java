package infrastructure.persistence.interfaces;

import domain.core.Transaction;
import java.util.List;

/**
 * Repository interface for Transaction data access.
 *
 * SOLID Principles:
 * - Single Responsibility: Only transaction persistence
 * - Dependency Inversion: Services depend on this interface
 */
public interface TransactionRepository {

    /**
     * Saves a new transaction.
     * @param transaction Transaction to save
     */
    void save(Transaction transaction);

    /**
     * Finds transaction by ID.
     * @param transactionId Transaction ID
     * @return Transaction object or null if not found
     */
    Transaction findById(String transactionId);

    /**
     * Finds all transactions for an account.
     * Used to get transaction history.
     * @param accountId Account ID
     * @return List of transactions involving this account
     */
    List<Transaction> findByAccountId(String accountId);

    /**
     * Gets all transactions.
     * @return List of all transactions
     */
    List<Transaction> findAll();
}