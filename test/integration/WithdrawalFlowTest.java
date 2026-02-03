package integration;

import application.service.*;
import application.strategy.OptimalCashDispensingStrategy;
import domain.core.*;
import domain.exception.ATMResourceException;
import domain.exception.InsufficientFundsException;
import infrastructure.factory.UserFactory;
import infrastructure.persistence.interfaces.*;
import infrastructure.persistence.json.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for complete withdrawal workflow.
 * Tests interaction between multiple components (services, repositories, ATM).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WithdrawalFlowTest {

    private static final String TEST_DATA_DIR = "test-data/";

    private TransactionService transactionService;
    private AuthenticationService authService;
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private Customer testCustomer;
    private Account testAccount;

    @BeforeAll
    void setUpAll() {
        // Create test data directory
        new File(TEST_DATA_DIR).mkdirs();
    }

    @BeforeEach
    void setUp() {
        // Reset ATM singleton for clean test
        ATM.resetInstance();

        // Initialize repositories with test directory
        userRepository = new JsonUserRepository(TEST_DATA_DIR);
        accountRepository = new JsonAccountRepository(TEST_DATA_DIR);
        TransactionRepository transactionRepository = new JsonTransactionRepository(TEST_DATA_DIR);
        ATMRepository atmRepository = new JsonATMRepository(TEST_DATA_DIR);

        // Initialize services
        authService = new AuthenticationService(userRepository);
        transactionService = new TransactionService(
                accountRepository,
                transactionRepository,
                atmRepository,
                new OptimalCashDispensingStrategy()
        );

        // Create test customer and account
        testCustomer = (Customer) UserFactory.createUser("TEST001", "TestUser", "1234", Role.CUSTOMER);
        testAccount = new Account("TESTACC", "TEST001", AccountType.CHECKING, 200.0);
        testCustomer.addAccountId("TESTACC");

        // Save to repository
        userRepository.save(testCustomer);
        accountRepository.save(testAccount);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data files
        try {
            Files.deleteIfExists(Paths.get(TEST_DATA_DIR + "users.json"));
            Files.deleteIfExists(Paths.get(TEST_DATA_DIR + "accounts.json"));
            Files.deleteIfExists(Paths.get(TEST_DATA_DIR + "transactions.json"));
            Files.deleteIfExists(Paths.get(TEST_DATA_DIR + "atm.json"));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @AfterAll
    void tearDownAll() {
        // Remove test data directory
        new File(TEST_DATA_DIR).delete();
    }

    @Test
    @DisplayName("Complete withdrawal flow: authenticate, withdraw, verify balance")
    void testCompleteWithdrawalFlow() {
        // Step 1: Authenticate user
        User authenticatedUser = authService.authenticate("TestUser", "1234");
        assertNotNull(authenticatedUser);
        assertEquals("TestUser", authenticatedUser.getName());

        // Step 2: Check initial balance
        double initialBalance = transactionService.getBalance("TESTACC");
        assertEquals(200.0, initialBalance);

        // Step 3: Perform withdrawal
        Transaction transaction = transactionService.withdraw("TESTACC", 50.0);

        // Step 4: Verify transaction
        assertNotNull(transaction);
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(50.0, transaction.getAmount());
        assertEquals("TESTACC", transaction.getFromAccountId());

        // Step 5: Verify balance updated
        double newBalance = transactionService.getBalance("TESTACC");
        assertEquals(150.0, newBalance);

        // Step 6: Verify ATM cash decreased
        ATM atm = ATM.getInstance();
        assertEquals(300.0, atm.getTotalCash()); // Was 350, withdrew 50
    }

    @Test
    @DisplayName("Withdrawal should fail with insufficient funds")
    void testWithdrawalInsufficientFunds() {
        // Attempt to withdraw more than balance
        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> transactionService.withdraw("TESTACC", 500.0)
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));

        // Verify balance unchanged
        double balance = transactionService.getBalance("TESTACC");
        assertEquals(200.0, balance);
    }

    @Test
    @DisplayName("Withdrawal should fail when ATM out of cash")
    void testWithdrawalATMOutOfCash() {
        // Drain ATM cash
        ATM atm = ATM.getInstance();
        transactionService.withdraw("TESTACC", 200.0); // Withdraw 200
        // ATM now has 150 (started with 350)

        // Create new account with high balance
        Account richAccount = new Account("RICH001", "TEST001", AccountType.CHECKING, 500.0);
        accountRepository.save(richAccount);

        // Try to withdraw more than ATM has
        ATMResourceException exception = assertThrows(
                ATMResourceException.class,
                () -> transactionService.withdraw("RICH001", 200.0)
        );

        assertTrue(exception.getMessage().contains("ATM out of cash"));
    }

    @Test
    @DisplayName("Multiple withdrawals should update balance correctly")
    void testMultipleWithdrawals() {
        // Perform multiple withdrawals
        transactionService.withdraw("TESTACC", 50.0);
        transactionService.withdraw("TESTACC", 30.0);
        transactionService.withdraw("TESTACC", 20.0);

        // Verify final balance
        double finalBalance = transactionService.getBalance("TESTACC");
        assertEquals(100.0, finalBalance); // 200 - 50 - 30 - 20 = 100

        // Verify ATM cash
        ATM atm = ATM.getInstance();
        assertEquals(250.0, atm.getTotalCash()); // 350 - 100 = 250
    }
}