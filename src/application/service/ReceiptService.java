package application.service;

import domain.core.ATM;
import domain.core.Transaction;
import domain.exception.ATMResourceException;
import infrastructure.persistence.interfaces.ATMRepository;

/**
 * Handles receipt printing for transactions and balance inquiries.
 * Bank Actor Involvement: Provides transaction data, no involvement in printing
 */
public class ReceiptService {

    private final ATM atm;
    private final ATMRepository atmRepository;

    /**
     * Constructor with dependency injection.
     */
    public ReceiptService(ATMRepository atmRepository) {
        this.atm = ATM.getInstance();
        this.atmRepository = atmRepository;
    }

    /**
     * Prints transaction receipt.
     * Consumes 1% ink and 1 sheet of paper.
     */
    public void printTransactionReceipt(Transaction transaction) {
        // Check ATM can print
        if (!atm.canPrintReceipt()) {
            if (atm.getInkLevel() <= 0) {
                throw new ATMResourceException("Cannot print receipt - ATM out of ink");
            }
            if (atm.getPaperLevel() <= 0) {
                throw new ATMResourceException("Cannot print receipt - ATM out of paper");
            }
        }

        // Print receipt
        System.out.println(transaction.generateReceipt());

        // Consume resources (1% ink, 1 sheet paper)
        atm.useReceiptResources();
        atmRepository.save(atm);  // ← SAVE ATM STATE

        System.out.println("Receipt printed.");
    }

    /**
     * Prints balance receipt.
     * Consumes 1% ink and 1 sheet of paper.
     */
    public void printBalanceReceipt(String accountId, double balance) {
        // Check ATM can print
        if (!atm.canPrintReceipt()) {
            if (atm.getInkLevel() <= 0) {
                throw new ATMResourceException("Cannot print receipt - ATM out of ink");
            }
            if (atm.getPaperLevel() <= 0) {
                throw new ATMResourceException("Cannot print receipt - ATM out of paper");
            }
        }

        // Print balance receipt
        System.out.println("================================");
        System.out.println("       BALANCE RECEIPT          ");
        System.out.println("================================");
        System.out.println("Account: " + accountId);
        System.out.println("Balance: €" + String.format("%.2f", balance));
        System.out.println("================================");

        // Consume resources
        atm.useReceiptResources();
        atmRepository.save(atm);  // ← SAVE ATM STATE

        System.out.println("Receipt printed.");
    }

    /**
     * Prints maintenance report (technician operations).
     * Does NOT consume ink/paper (printed to console only).
     */
    public void printMaintenanceReport(String action, String technicianName) {
        System.out.println("================================");
        System.out.println("    MAINTENANCE REPORT          ");
        System.out.println("================================");
        System.out.println("Action: " + action);
        System.out.println("Technician: " + technicianName);
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
        System.out.println("================================");
        System.out.println("Maintenance report printed.");
    }
}