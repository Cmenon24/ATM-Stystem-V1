package domain.core;

import java.util.ArrayList;
import java.util.List;

/**Extends User and adds customer-specific functionality.
 * SOLID Principles:
 * - Single Responsibility: Manages customer data and account associations
 * - Liskov Substitution: Can be used wherever User is expected
 *
 * OOP Pillars:
 * - Inheritance: Extends User class
 * - Encapsulation: Private account list with controlled access
 * - Polymorphism: Implements displayMenu() with customer-specific behavior
 */
public class Customer extends User {
    /**
     * List of account IDs belonging to this customer
     * Each customer has checking and savings accounts
     */
    private List<String> accountIds;

    /**
     * Constructor for creating a new customer.
     *
     * @param userId Unique customer identifier
     * @param name Customer's name
     * @param pin Customer's PIN for authentication
     */
    public Customer(String userId, String name, String pin) {
        super(userId, name, pin, Role.CUSTOMER);
        this.accountIds = new ArrayList<>();
    }

    /**
     * Default constructor for JSON deserialization.
     */
    public Customer() {
        super();
        this.accountIds = new ArrayList<>();
        this.setRole(Role.CUSTOMER);
    }

    /**
     * Gets the list of account IDs belonging to this customer.
     *
     * @return List of account IDs
     */
    public List<String> getAccountIds() {
        return accountIds;
    }

    /**
     * Sets the list of account IDs for this customer.
     *
     * @param accountIds List of account IDs to set
     */
    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    /**
     * Adds an account ID to this customer's account list.
     *
     * @param accountId Account ID to add
     */
    public void addAccountId(String accountId) {
        if (accountId != null && !accountIds.contains(accountId)) {
            this.accountIds.add(accountId);
        }
    }

    /**
     * Checks if this customer has a specific account.
     *
     * @param accountId Account ID to check
     * @return true if customer has this account, false otherwise
     */
    public boolean hasAccount(String accountId) {
        return accountIds.contains(accountId);
    }

    /**
     * Displays customer-specific menu options.
     * This method will be called by the presentation layer.
     *
     * OOP Pillar: Polymorphism - Customer-specific implementation
     * Note: The actual menu display logic is in the presentation layer (CustomerMenu.java)
     */
    @Override
    public void displayMenu() {
        // Implementation delegated to presentation layer (CustomerMenu)
        // This method exists to satisfy the abstract contract from User
        System.out.println("Customer Menu:");
        System.out.println("1. Check Balance");
        System.out.println("2. Withdraw Cash");
        System.out.println("3. Deposit Funds");
        System.out.println("4. Transfer Funds");
        System.out.println("5. Exit");
    }

    @Override
    public String toString() {
        return "Customer{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getName() + '\'' +
                ", accountIds=" + accountIds +
                '}';
    }
}