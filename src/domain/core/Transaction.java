package domain.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Transaction represents a banking transaction in the ATM system.
 *
 * SOLID Principles:
 * - Single Responsibility: Manages transaction data only
 * - Open/Closed: Can be extended for new transaction types
 *
 * OOP Pillars:
 * - Encapsulation: Private fields with controlled access
 * - Abstraction: Hides transaction complexity from clients
 */

public class Transaction {
    private String transactionId;
    private TransactionType type;
    private double amount;
    private String fromAccountId;
    private String toAccountId;
    private LocalDateTime timestamp;

    /**
     * Full constructor for creating a new transaction.
     */
    public Transaction(String transactionId, TransactionType type, double amount,
                       String fromAccountId, String toAccountId, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.timestamp = timestamp;
    }

    /**
     * Simplified constructor for creating transactions with auto-generated ID and timestamp.
     */
    public Transaction(TransactionType type, double amount, String fromAccountId, String toAccountId) {
        this.transactionId = generateShortTransactionId();
        this.type = type;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Default constructor for JSON deserialization.
     */
    public Transaction() {
    }

    /**
     * Generates a short transaction ID in format: XXXXXXXX-XXXX
     * Example: A3F5B2C7-1D4E
     *
     * @return Short transaction ID (13 characters)
     */
    private String generateShortTransactionId() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        // Take first 8 characters, hyphen, then next 4 characters
        return uuid.substring(0, 8) + "-" + uuid.substring(8, 12);
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Formats the timestamp for display on receipts.
     *
     * @return Formatted timestamp string
     */
    public String getFormattedTimestamp() {
        if (timestamp == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    /**
     * Generates a receipt string for this transaction.
     * Used by ReceiptService to print transaction receipts.
     *
     * @return Formatted receipt string
     */
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("================================\n");
        receipt.append("       TRANSACTION RECEIPT       \n");
        receipt.append("================================\n");
        receipt.append("Transaction ID: ").append(transactionId).append("\n");
        receipt.append("Type: ").append(type).append("\n");
        receipt.append("Amount: €").append(String.format("%.2f", amount)).append("\n");
        receipt.append("From Account: ").append(fromAccountId != null ? fromAccountId : "N/A").append("\n");
        receipt.append("To Account: ").append(toAccountId != null ? toAccountId : "N/A").append("\n");
        receipt.append("Timestamp: ").append(getFormattedTimestamp()).append("\n");
        receipt.append("================================\n");
        return receipt.toString();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", type=" + type +
                ", amount=" + String.format("%.2f", amount) +
                ", fromAccountId='" + fromAccountId + '\'' +
                ", toAccountId='" + toAccountId + '\'' +
                ", timestamp=" + getFormattedTimestamp() +
                '}';
    }
}