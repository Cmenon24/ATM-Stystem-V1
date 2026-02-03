package infrastructure.persistence.json;

import com.google.gson.reflect.TypeToken;
import domain.core.Transaction;
import infrastructure.persistence.interfaces.TransactionRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON implementation of TransactionRepository.
 * Stores transactions in transactions.json file.
 *
 * File: data/transactions.json
 * Format: JSON array of Transaction objects
 */
public class JsonTransactionRepository implements TransactionRepository {

    private static final String FILENAME = "transactions.json";
    private final JsonFileHandler fileHandler;

    public JsonTransactionRepository(String dataDirectory) {
        this.fileHandler = new JsonFileHandler(dataDirectory);
    }

    /**
     * Loads all transactions from JSON file.
     */
    private List<Transaction> loadTransactions() {
        Type listType = new TypeToken<ArrayList<Transaction>>(){}.getType();
        List<Transaction> transactions = fileHandler.readFromFile(FILENAME, listType);
        return transactions != null ? transactions : new ArrayList<>();
    }

    /**
     * Saves all transactions to JSON file.
     */
    private void saveTransactions(List<Transaction> transactions) {
        fileHandler.writeToFile(FILENAME, transactions);
    }

    @Override
    public void save(Transaction transaction) {
        List<Transaction> transactions = loadTransactions();

        // Add transaction (Bank records)
        transactions.add(transaction);

        // Save to file
        saveTransactions(transactions);
    }

    @Override
    public Transaction findById(String transactionId) {
        List<Transaction> transactions = loadTransactions();
        return transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Transaction> findByAccountId(String accountId) {
        List<Transaction> transactions = loadTransactions();

        // Find transactions where account is source OR destination
        return transactions.stream()
                .filter(t -> accountId.equals(t.getFromAccountId()) ||
                        accountId.equals(t.getToAccountId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll() {
        return loadTransactions();
    }
}