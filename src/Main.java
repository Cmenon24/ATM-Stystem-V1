import application.service.*;
import application.strategy.OptimalCashDispensingStrategy;
import domain.core.*;
import infrastructure.factory.UserFactory;
import infrastructure.persistence.interfaces.*;
import infrastructure.persistence.json.*;
import presentation.cli.ATMController;

import java.util.ArrayList;
import java.util.List;

/**
 * Main - VERSION 1.
 *
 * Version 1 Features:
 * - Customer: Full access too balance, withdraw, deposit, transfer
 * - Technician: Read-only access for ATM status
 *
 * Architecture: Uncle Bobs Clean Architecture
 * - Initializes all layers
 * - Injects dependencies
 * - Starts ATM controller
 *
 * SOLID Principle: Dependency Injection - Main creates and injects all dependencies
 */
public class Main {

    private static final String DATA_DIRECTORY = "data/";

    public static void main(String[] args) {
        System.out.println("Initializing ATM System (Version 1)...\n");

        // Step 1: Initialize repositories (Infrastructure Layer)
        UserRepository userRepository = new JsonUserRepository(DATA_DIRECTORY);
        AccountRepository accountRepository = new JsonAccountRepository(DATA_DIRECTORY);
        TransactionRepository transactionRepository = new JsonTransactionRepository(DATA_DIRECTORY);
        ATMRepository atmRepository = new JsonATMRepository(DATA_DIRECTORY);

        // Step 2: Initialize data (first run only - checks if data exists)
        initializeData(userRepository, accountRepository,atmRepository);

        //Load ATM state from persistence
        atmRepository.load();

        // Step 3: Initialize services (Application Layer)
        AuthenticationService authService = new AuthenticationService(userRepository);

        TransactionService transactionService = new TransactionService(
                accountRepository,
                transactionRepository,
                atmRepository,
                new OptimalCashDispensingStrategy()  // Strategy Pattern
        );

        // VERSION 1: Use MaintenanceServiceV1 (restricted access)
        MaintenanceService maintenanceService = new MaintenanceServiceV1(atmRepository);

        ReceiptService receiptService = new ReceiptService(atmRepository);

        // Step 4: Initialize controller (Presentation Layer)
        ATMController controller = new ATMController(
                authService,
                transactionService,
                maintenanceService,
                receiptService
        );

        // Step 5: Start ATM
        System.out.println("ATM System Ready!\n");
        controller.start();
    }

    /**
     * Initializes data if not alreeady present.
     * Creates pre-populated users and accounts for testing.
     */
    private static void initializeData(UserRepository userRepository, AccountRepository accountRepository, ATMRepository atmRepository) {
        // Check if users already exist
        List<User> existingUsers = userRepository.findAll();

        if (!existingUsers.isEmpty()) {
            System.out.println("Data already initialized. Loading existing data...\n");
            return;
        }

        System.out.println("First run detected. Initializing data...\n");

        // Create users using Factory Pattern
        Customer john = (Customer) UserFactory.createUser("USER001", "John", "1234", Role.CUSTOMER);
        Technician jake = (Technician) UserFactory.createUser("USER002", "Jake", "9999", Role.TECHNICIAN);

        // Create accounts for John
        Account johnChecking = new Account("ACC001C", "USER001", AccountType.CHECKING, 100.0);
        Account johnSavings = new Account("ACC001S", "USER001", AccountType.SAVINGS, 50.0);
        john.addAccountId("ACC001C");
        john.addAccountId("ACC001S");

        // Save users
        userRepository.save(john);
        userRepository.save(jake);

        // Save accounts
        accountRepository.save(johnChecking);
        accountRepository.save(johnSavings);

        // Save initial ATM state
        ATM atm = ATM.getInstance();
        atmRepository.save(atm);

        System.out.println("✓ Users created:");
        System.out.println("  - John (Customer, PIN: 1234)");
        System.out.println("  - Jake (Technician, PIN: 9999)");

        System.out.println("\n✓ Accounts created:");
        System.out.println("  - John: Checking (€100), Savings (€50)");

        System.out.println("\n✓ ATM initialized:");
        System.out.println("  - Cash: €350");
        System.out.println("  - Ink: 5%");
        System.out.println("  - Paper: 6 sheets");
        System.out.println("  - Software: Version 1.0");

        System.out.println("\nInitialization complete!\n");
    }
}