package presentation.cli;

import application.service.AuthenticationService;
import application.service.MaintenanceService;
import application.service.ReceiptService;
import application.service.TransactionService;
import domain.core.Customer;
import domain.core.Role;
import domain.core.Technician;
import domain.core.User;
import domain.exception.InvalidPinException;

import java.util.Scanner;

/**
 * Main controller for ATM CLI application.
 * Handles login flow and routes to appropriate menu.
 *
 * Flow:
 * 1. Displays main menu
 * 2. Authenticate user
 * 3. Route to CustomerMenu or TechnicianMenu based on role
 * 4. Return to main menu after logout
 *
 * SOLID Principle: Single Responsibility - Only handles main flow routing
 */
public class ATMController {

    private final AuthenticationService authService;
    private final TransactionService transactionService;
    private final MaintenanceService maintenanceService;
    private final ReceiptService receiptService;
    private final Scanner scanner;

    /**
     * Constructor with dependency injection.
     * All services are provided by Main.java initialization.
     */
    public ATMController(AuthenticationService authService,
                         TransactionService transactionService,
                         MaintenanceService maintenanceService,
                         ReceiptService receiptService) {
        this.authService = authService;
        this.transactionService = transactionService;
        this.maintenanceService = maintenanceService;
        this.receiptService = receiptService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the ATM application.
     * Main loop: show menu, handle login, route to appropriate menu.
     */
    public void start() {
        System.out.println("========================================");
        System.out.println("   WELCOME TO ATM BANKING SYSTEM");
        System.out.println("========================================\n");

        boolean running = true;

        while (running) {
            // Display main menu
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Customer Login");
            System.out.println("2. Technician Login");
            System.out.println("3. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleCustomerLogin();
                    break;

                case "2":
                    handleTechnicianLogin();
                    break;

                case "3":
                    System.out.println("\nThank you for using our ATM. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    /**
     * Handles customer login and routes to customer menu.
     */
    private void handleCustomerLogin() {
        System.out.println("\n=== CUSTOMER LOGIN ===");

        try {
            // Get credentials
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            // Authenticate (Bank validates credentials)
            User user = authService.authenticate(name, pin);

            // Verify user is a customer
            if (user.getRole() != Role.CUSTOMER) {
                System.out.println("Error: These credentials are not for a customer account.");
                return;
            }

            // Cast to Customer
            Customer customer = (Customer) user;

            // Success message
            System.out.println("Authentication successful. Welcome " + customer.getName() + "!");

            // Route to customer menu
            CustomerMenu customerMenu = new CustomerMenu(customer, transactionService, receiptService, scanner);
            customerMenu.show();

        } catch (InvalidPinException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    /**
     * Handles technician login and routes to technician menu.
     */
    private void handleTechnicianLogin() {
        System.out.println("\n=== TECHNICIAN LOGIN ===");

        try {
            // Get credentials
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            // Authenticate (local ATM validation, no Bank)
            User user = authService.authenticate(name, pin);

            // Verify user is a technician
            if (user.getRole() != Role.TECHNICIAN) {
                System.out.println("Error: These credentials are not for a technician account.");
                return;
            }

            // Cast to Technician
            Technician technician = (Technician) user;

            // Success message
            System.out.println("Authentication successful. Welcome " + technician.getName() + "!");

            // Route to technician menu
            TechnicianMenu technicianMenu = new TechnicianMenu(technician, maintenanceService, receiptService, scanner);
            technicianMenu.show();

        } catch (InvalidPinException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }
}