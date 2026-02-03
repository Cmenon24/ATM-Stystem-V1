package domain.core;

/**
 * Account is a bank account in the ATM system.
 *
 * SOLID Principles:
 * - Single Responsibility: Manages account balance and basic operations
 * - Open/Closed: Can be extended for different account types without modification
 *
 * OOP Pillars:
 * - Encapsulation: Private balance with controlled access through methods
 * - Abstraction: Exposes high-level operations (withdraw, deposit) hiding implementation
 **/

public class Account {
    private String accountId;
    private String userId;
    private AccountType accountType;
    private double balance;

    /**
     * Constructor for creating a new account.
     */
    public Account(String accountId, String userId, AccountType accountType, double balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
    }

    /**
     * Default constructor for JSON deserialization.
     */
    public Account() {
    }

    // Getters and Setters
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public AccountType getAccountType() {
        return accountType;
    }
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Business Logic Methods

    /**
     * Withdraws money from the account.
     * Validates sufficient funds before withdrawal.
     * @param amount Amount to withdraw
     * @return true if withdrawal successful, false if insufficient funds
     */
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    /**
     * Deposits money into the account.
     * Bank actor records this transaction.
     *
     * @param amount Amount to deposit
     * @return true if deposit successful, false if invalid amount
     */
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        this.balance += amount;
        return true;
    }

    /**
     * Checks if account has sufficient funds for a transaction.
     * Bank actor uses this for validation.
     *
     * @param amount Amount to check
     * @return true if sufficient funds, false otherwise
     */
    public boolean hasSufficientFunds(double amount) {
        return this.balance >= amount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", userId='" + userId + '\'' +
                ", accountType=" + accountType +
                ", balance=" + String.format("%.2f", balance) +
                '}';
    }
}