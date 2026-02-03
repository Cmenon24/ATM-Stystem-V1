package presentation.cli;

import application.service.ReceiptService;
import application.service.TransactionService;
import domain.core.*;
import domain.exception.ATMResourceException;
import domain.exception.InsufficientFundsException;

import java.util.List;
import java.util.Scanner;

/**
 * Customer menu
 * Handles: Check balance, withdraw, deposit, transferr
 *
 * SOLID Principle: Single Responsibility - Only handles customer UI
 */
public class CustomerMenu {

    private final Customer customer;
    private final TransactionService transactionService;
    private final ReceiptService receiptService;
    private final Scanner scanner;

    /**
     * Constructor with dependency injection.
     */
    public CustomerMenu(Customer customer,
                        TransactionService transactionService,
                        ReceiptService receiptService,
                        Scanner scanner) {
        this.customer = customer;
        this.transactionService = transactionService;
        this.receiptService = receiptService;
        this.scanner = scanner;
    }

    /**
     * Displays customer menu and handles operations.
     */
    public void show() {
        boolean running = true;

        while (running) {
            // Display menu
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw Cash");
            System.out.println("3. Deposit Funds");
            System.out.println("4. Transfer Funds");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleCheckBalance();
                    break;

                case "2":
                    handleWithdraw();
                    break;

                case "3":
                    handleDeposit();
                    break;

                case "4":
                    handleTransfer();
                    break;

                case "5":
                    System.out.println("Thank you for using our ATM. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Handles balance inquiry.
     * Bank provides current balance.
     */
    private void handleCheckBalance() {
        System.out.println("\n=== CHECK BALANCE ===");

        // Select account
        String accountId = selectAccount();
        if (accountId == null) return;

        try {
            // Get balance from Bank
            double balance = transactionService.getBalance(accountId);

            System.out.println("\nCurrent balance: €" + String.format("%.2f", balance));

            // Offer receipt
            System.out.print("Would you like a receipt? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();

            if (response.equals("Y")) {
                receiptService.printBalanceReceipt(accountId, balance);
            }

        } catch (ATMResourceException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error checking balance: " + e.getMessage());
        }
    }

    /**
     * Handles cash withdrawal.
     * Bank validates funds and records transaction.
     */
    private void handleWithdraw() {
        System.out.println("\n=== WITHDRAW CASH ===");

        // Select account
        String accountId = selectAccount();
        if (accountId == null) return;

        try {
            // Get amount
            System.out.print("Enter amount to withdraw (multiples of €10): €");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // Validate amount
            if (amount <= 0 || amount % 10 != 0) {
                System.out.println("Error: Amount must be positive and multiple of €10");
                return;
            }

            // Perform withdrawal (Bank validates and records)
            Transaction transaction = transactionService.withdraw(accountId, amount);

            System.out.println("\nWithdrawal successful!");
            System.out.println("Please take your cash: €" + String.format("%.2f", amount));

            // Offer receipt
            System.out.print("Would you like a receipt? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();

            if (response.equals("Y")) {
                receiptService.printTransactionReceipt(transaction);
            }

        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ATMResourceException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount entered");
        } catch (Exception e) {
            System.out.println("Error during withdrawal: " + e.getMessage());
        }
    }

    /**
     * Handles deposit.
     * Bank updates balance and records transaction.
     */
    private void handleDeposit() {
        System.out.println("\n=== DEPOSIT FUNDS ===");

        // Select account
        String accountId = selectAccount();
        if (accountId == null) return;

        try {
            // Get amount
            System.out.print("Enter amount to deposit: €");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // Validate amount
            if (amount <= 0) {
                System.out.println("Error: Amount must be positive");
                return;
            }

            // Perform deposit (Bank records)
            Transaction transaction = transactionService.deposit(accountId, amount);

            System.out.println("\nDeposit successful!");
            System.out.println("€" + String.format("%.2f", amount) + " deposited.");

            // Offer receipt
            System.out.print("Would you like a receipt? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();

            if (response.equals("Y")) {
                receiptService.printTransactionReceipt(transaction);
            }

        } catch (ATMResourceException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount entered");
        } catch (Exception e) {
            System.out.println("Error during deposit: " + e.getMessage());
        }
    }

    /**
     * Handles transfer between customer's own accounts.
     * Bank validates and records transfer.
     */
    private void handleTransfer() {
        System.out.println("\n=== TRANSFER FUNDS ===");

        // Get customer's accounts
        List<String> accountIds = customer.getAccountIds();

        if (accountIds.size() < 2) {
            System.out.println("Error: You need at least 2 accounts to transfer.");
            return;
        }

        try {
            // Select source account
            System.out.println("\nTransfer FROM:");
            String fromAccountId = selectAccount();
            if (fromAccountId == null) return;

            // Select destination account
            System.out.println("\nTransfer TO:");
            String toAccountId = selectAccount();
            if (toAccountId == null) return;

            // Validate different accounts
            if (fromAccountId.equals(toAccountId)) {
                System.out.println("Error: Cannot transfer to the same account");
                return;
            }

            // Get amount
            System.out.print("\nEnter amount to transfer: €");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // Validate amount
            if (amount <= 0) {
                System.out.println("Error: Amount must be positive");
                return;
            }

            // Perform transfer (Bank validates and records)
            Transaction transaction = transactionService.transfer(fromAccountId, toAccountId, amount);

            System.out.println("\nTransfer successful!");
            System.out.println("€" + String.format("%.2f", amount) + " transferred.");

            // Offer receipt
            System.out.print("Would you like a receipt? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();

            if (response.equals("Y")) {
                receiptService.printTransactionReceipt(transaction);
            }

        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount entered");
        } catch (Exception e) {
            System.out.println("Error during transfer: " + e.getMessage());
        }
    }

    /**
     * Helper method to select account.
     *
     * @return Selected account ID or null if cancelled
     */
    private String selectAccount() {
        List<String> accountIds = customer.getAccountIds();

        if (accountIds.isEmpty()) {
            System.out.println("Error: No accounts found for this customer");
            return null;
        }

        System.out.println("Select account:");
        for (int i = 0; i < accountIds.size(); i++) {
            // Determine account type from ID (simple logic: ends with 'C' = checking, 'S' = savings)
            String accountId = accountIds.get(i);
            String type = accountId.endsWith("C") ? "Checking" : "Savings";
            System.out.println((i + 1) + ". " + type + " (" + accountId + ")");
        }

        System.out.print("Select option: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice < 1 || choice > accountIds.size()) {
                System.out.println("Invalid selection");
                return null;
            }

            return accountIds.get(choice - 1);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
            return null;
        }
    }
}