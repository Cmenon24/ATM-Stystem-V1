package application.service;

import application.strategy.CashDispensingStrategy;
import domain.core.*;
import domain.exception.ATMResourceException;
import domain.exception.InsufficientFundsException;
import infrastructure.persistence.interfaces.AccountRepository;
import infrastructure.persistence.interfaces.ATMRepository;
import infrastructure.persistence.interfaces.TransactionRepository;

import java.util.List;
import java.util.Map;

/**
 * Handles all banking transactions (withdraw, deposit, transfer, balance).
 */
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ATMRepository atmRepository;
    private final CashDispensingStrategy dispensingStrategy;
    private final ATM atm;

    /**
     * Constructor with dependency injection.
     */
    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              ATMRepository atmRepository,
                              CashDispensingStrategy dispensingStrategy) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.atmRepository = atmRepository;
        this.dispensingStrategy = dispensingStrategy;
        this.atm = ATM.getInstance();
    }

    /**
     * Gets account balance.
     */
    public double getBalance(String accountId) {
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        return account.getBalance();
    }

    /**
     * Withdraws cash from account.
     */
    public Transaction withdraw(String accountId, double amount) {
        // Step 1: Validate account and balance
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        if (!account.hasSufficientFunds(amount)) {
            throw new InsufficientFundsException(
                    String.format("Insufficient funds. Current balance: €%.2f, Requested: €%.2f",
                            account.getBalance(), amount)
            );
        }

        // Step 2: Check ATM has enough total cash
        if (!atm.hasSufficientCash(amount)) {
            throw new ATMResourceException("ATM out of cash. Please try another ATM.");
        }

        // Step 3: Calculate note distribution
        Map<Integer, Integer> notes = dispensingStrategy.dispense(amount, atm.getCashByDenomination());
        if (notes == null) {
            throw new ATMResourceException("Cannot dispense exact amount with available denominations.");
        }

        // Step 4: Update account balance
        account.withdraw(amount);
        accountRepository.update(account);

        // Step 5: Update ATM cash reserves
        for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
            atm.removeCash(entry.getKey(), entry.getValue());
        }
        atmRepository.save(atm);  // ← SAVE ATM STATE

        // Step 6: Record transaction
        Transaction transaction = new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                accountId,
                null
        );
        transactionRepository.save(transaction);

        return transaction;
    }

    /**
     * Deposits funds into account.
     */
    public Transaction deposit(String accountId, double amount) {
        // Step 1: Validate account
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        // Step 2: Update account balance
        account.deposit(amount);
        accountRepository.update(account);

        // Step 3: Update ATM cash reserves - distribute into denominations intelligently
        int amountRemaining = (int) amount;

        int hundreds = amountRemaining / 100;
        amountRemaining = amountRemaining % 100;

        int fifties = amountRemaining / 50;
        amountRemaining = amountRemaining % 50;

        int twenties = amountRemaining / 20;
        amountRemaining = amountRemaining % 20;

        int tens = amountRemaining / 10;

        // Add each denomination to ATM
        if (hundreds > 0) atm.addCash(100, hundreds);
        if (fifties > 0) atm.addCash(50, fifties);
        if (twenties > 0) atm.addCash(20, twenties);
        if (tens > 0) atm.addCash(10, tens);

        // Save ATM state
        atmRepository.save(atm);

        // Step 4: Record transaction
        Transaction transaction = new Transaction(
                TransactionType.DEPOSIT,
                amount,
                null,
                accountId
        );
        transactionRepository.save(transaction);

        return transaction;
    }

    /**
     * Transfers funds between customer's own accounts.
     */
    public Transaction transfer(String fromAccountId, String toAccountId, double amount) {
        // Step 1: Validate accounts exist
        Account fromAccount = accountRepository.findById(fromAccountId);
        Account toAccount = accountRepository.findById(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("One or both accounts not found");
        }

        // Step 2: Check sufficient funds
        if (!fromAccount.hasSufficientFunds(amount)) {
            throw new InsufficientFundsException(
                    String.format("Insufficient funds in source account. Balance: €%.2f", fromAccount.getBalance())
            );
        }

        // Step 3: Debit source account
        fromAccount.withdraw(amount);
        accountRepository.update(fromAccount);

        // Step 4: Credit destination account
        toAccount.deposit(amount);
        accountRepository.update(toAccount);

        // Step 5: Record transaction
        Transaction transaction = new Transaction(
                TransactionType.TRANSFER,
                amount,
                fromAccountId,
                toAccountId
        );
        transactionRepository.save(transaction);

        return transaction;
    }

    /**
     * Gets transaction history for an account.
     */
    public List<Transaction> getTransactionHistory(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}