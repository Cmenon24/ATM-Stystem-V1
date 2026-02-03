package presentation.cli;

import application.service.MaintenanceService;
import application.service.ReceiptService;
import domain.core.Technician;
import domain.core.ATM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Technician menu for maintenance operationss.
 * Version 1: Read-only access for view ATM status
 * Version 2: Will allow to extend maintanance vor Version 2
 */
public class TechnicianMenu {

    private final Technician technician;
    private final MaintenanceService maintenanceService;
    private final ReceiptService receiptService;
    private final Scanner scanner;
    private final List<String> maintenanceActions; // Track all actions in session

    public TechnicianMenu(Technician technician,
                          MaintenanceService maintenanceService,
                          ReceiptService receiptService,
                          Scanner scanner) {
        this.technician = technician;
        this.maintenanceService = maintenanceService;
        this.receiptService = receiptService;
        this.scanner = scanner;
        this.maintenanceActions = new ArrayList<>();
    }

    /**
     * Displays technician menu and handles operations.
     */
    public void show() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== TECHNICIAN MENU ===");
            System.out.println("1. View ATM Status");
            System.out.println("2. Replenish Cash");
            System.out.println("3. Refill Ink");
            System.out.println("4. Restock Paper");
            System.out.println("5. Update Software");
            System.out.println("6. Print Maintenance Report");
            System.out.println("7. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleViewStatus();
                    break;

                case "2":
                    if (!maintenanceService.isMaintenanceAllowed()) {
                        System.out.println("\n=== REPLENISH CASH ===");
                        System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
                    } else {
                        handleReplenishCash();
                    }
                    break;

                case "3":
                    if (!maintenanceService.isMaintenanceAllowed()) {
                        System.out.println("\n=== REFILL INK ===");
                        System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
                    } else {
                        handleRefillInk();
                    }
                    break;

                case "4":
                    if (!maintenanceService.isMaintenanceAllowed()) {
                        System.out.println("\n=== RESTOCK PAPER ===");
                        System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
                    } else {
                        handleRestockPaper();
                    }
                    break;

                case "5":
                    if (!maintenanceService.isMaintenanceAllowed()) {
                        System.out.println("\n=== UPDATE SOFTWARE ===");
                        System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
                    } else {
                        handleUpdateSoftware();
                    }
                    break;

                case "6":
                    handlePrintMaintenanceReport();
                    break;

                case "7":
                    System.out.println("Maintenance session ended. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * View ATM status is available for all versions.
     */
    private void handleViewStatus() {
        System.out.println("\n" + maintenanceService.getATMStatus());
    }

    /**
     * Replenish cash throws errors in V1 and should allow entry for V2 and show custom statusess
     */
    private void handleReplenishCash() {
        System.out.println("\n=== REPLENISH CASH ===");

        if (!maintenanceService.isMaintenanceAllowed()) {
            System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
            return;
        }

        try {
            ATM atm = ATM.getInstance();
            System.out.println(atm.getCashStatusReport());

            Map<Integer, Integer> denominations = new HashMap<>();

            // Get €100 notes
            System.out.print("Enter number of €100 notes to add: ");
            int count100 = Integer.parseInt(scanner.nextLine().trim());
            if (count100 < 0) {
                System.out.println("Error: Cannot add negative notes");
                return;
            }
            denominations.put(100, count100);

            // Get €50 notes
            System.out.print("Enter number of €50 notes to add: ");
            int count50 = Integer.parseInt(scanner.nextLine().trim());
            if (count50 < 0) {
                System.out.println("Error: Cannot add negative notes");
                return;
            }
            denominations.put(50, count50);

            // Get €20 notes
            System.out.print("Enter number of €20 notes to add: ");
            int count20 = Integer.parseInt(scanner.nextLine().trim());
            if (count20 < 0) {
                System.out.println("Error: Cannot add negative notes");
                return;
            }
            denominations.put(20, count20);

            // Get €10 notes
            System.out.print("Enter number of €10 notes to add: ");
            int count10 = Integer.parseInt(scanner.nextLine().trim());
            if (count10 < 0) {
                System.out.println("Error: Cannot add negative notes");
                return;
            }
            denominations.put(10, count10);

            // Calculate total being added
            int totalAdded = (count100 * 100) + (count50 * 50) + (count20 * 20) + (count10 * 10);

            // Validate at least one note added
            if (totalAdded == 0) {
                System.out.println("Error: Cash replenish cancelled. You did not add any notes");
                return;
            }

            // Perform replenishment
            maintenanceService.replenishCash(denominations);
            double newTotal = atm.getTotalCash();

            System.out.println("\nCash replenishment successful!");
            System.out.println("Added: €" + totalAdded);
            System.out.println("New total: €" + String.format("%.2f", newTotal));

            // Record action
            maintenanceActions.add(String.format("CASH_REPLENISHMENT: Added €%d (New total: €%.2f)",
                    totalAdded, newTotal));

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number entered");
        } catch (Exception e) {
            System.out.println("Error during cash replenishment: " + e.getMessage());
        }
    }

    /**
     * Refill ink - throws errors in V1 and should allow entry for V2
     */
    private void handleRefillInk() {
        System.out.println("\n=== REFILL INK ===");

        if (!maintenanceService.isMaintenanceAllowed()) {
            System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
            return;
        }

        try {
            ATM atm = ATM.getInstance();
            System.out.println(atm.getInkStatusReport());

            int oldLevel = atm.getInkLevel();

            System.out.print("Refill ink to 100%? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();

            if (!response.equals("Y") && !response.equals("N")) {
                System.out.println("Error: Please enter Y or N");
                return;
            }

            if (response.equals("Y")) {
                maintenanceService.refillInk();
                int newLevel = atm.getInkLevel();

                System.out.println("\nInk refilled successfully!");
                System.out.println("Previous level: " + oldLevel + "%");
                System.out.println("New level: " + newLevel + "%");

                // Record action
                maintenanceActions.add(String.format("INK_REFILL: %d%% → %d%%", oldLevel, newLevel));
            } else {
                System.out.println("Ink refill cancelled.");
            }

        } catch (Exception e) {
            System.out.println("Error during ink refill: " + e.getMessage());
        }
    }

    /**
     * Restock paper - V2 only.
     */
    private void handleRestockPaper() {
        System.out.println("\n=== RESTOCK PAPER ===");

        if (!maintenanceService.isMaintenanceAllowed()) {
            System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
            return;
        }

        try {
            ATM atm = ATM.getInstance();
            System.out.println(atm.getPaperStatusReport());

            int oldLevel = atm.getPaperLevel();

            System.out.print("Enter number of sheets to add: ");
            int sheets = Integer.parseInt(scanner.nextLine().trim());

            if (sheets <= 0) {
                System.out.println("Error: Paper restock cancelled. You did not enter a number greater than 0");
                return;
            }

            maintenanceService.restockPaper(sheets);
            int newLevel = atm.getPaperLevel();

            System.out.println("\nPaper restocked successfully!");
            System.out.println("Added: " + sheets + " sheets");
            System.out.println("Previous level: " + oldLevel + " sheets");
            System.out.println("New level: " + newLevel + " sheets");

            // Record action
            maintenanceActions.add(String.format("PAPER_RESTOCK: Added %d sheets (%d → %d sheets)",
                    sheets, oldLevel, newLevel));

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number entered");
        } catch (Exception e) {
            System.out.println("Error during paper restock: " + e.getMessage());
        }
    }

    /**
     * Update software - V2only
     */
    private void handleUpdateSoftware() {
        System.out.println("\n=== UPDATE SOFTWARE ===");

        if (!maintenanceService.isMaintenanceAllowed()) {
            System.out.println("Error: Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities.");
            return;
        }

        try {
            ATM atm = ATM.getInstance();
            System.out.println(atm.getSoftwareStatusReport());

            String oldVersion = atm.getSoftwareVersion();

            System.out.print("Enter new software version (e.g., 2.0): ");
            String newVersion = scanner.nextLine().trim();

            if (newVersion.isEmpty()) {
                System.out.println("Error: Version cannot be empty");
                return;
            }

            // Validate version is higher
            if (compareVersions(newVersion, oldVersion) <= 0) {
                System.out.println("Error: Update cancelled. New version must be higher than current version (" + oldVersion + ")");
                return;
            }

            maintenanceService.updateSoftware(newVersion);

            System.out.println("\nSoftware updated successfully!");
            System.out.println("Previous version: " + oldVersion);
            System.out.println("New version: " + newVersion);

            // Record action
            maintenanceActions.add(String.format("SOFTWARE_UPDATE: %s → %s", oldVersion, newVersion));

        } catch (Exception e) {
            System.out.println("Error during software update: " + e.getMessage());
        }
    }

    /**
     * Prints consolidated maintenance report showing all actions performed.
     */
    private void handlePrintMaintenanceReport() {
        System.out.println("\n=== MAINTENANCE REPORT ===");

        if (maintenanceActions.isEmpty()) {
            System.out.println("No maintenance actions performed in this session.");
            return;
        }

        System.out.println("================================");
        System.out.println("    MAINTENANCE REPORT          ");
        System.out.println("================================");
        System.out.println("Technician: " + technician.getName());
        System.out.println("Timestamp: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("\nActions Performed:");

        for (int i = 0; i < maintenanceActions.size(); i++) {
            System.out.println((i + 1) + ". " + maintenanceActions.get(i));
        }

        System.out.println("================================");
        System.out.println("Maintenance report printed to console.");
    }

    /**
     * Compares two version strings (like"1.0" vs "2.0").
     * Returns: negative if v1 < v2, zero if equal, positive if v1 > v2
     */
    private int compareVersions(String v1, String v2) {
        try {
            String[] parts1 = v1.split("\\.");
            String[] parts2 = v2.split("\\.");

            int major1 = Integer.parseInt(parts1[0]);
            int major2 = Integer.parseInt(parts2[0]);

            if (major1 != major2) {
                return major1 - major2;
            }

            if (parts1.length > 1 && parts2.length > 1) {
                int minor1 = Integer.parseInt(parts1[1]);
                int minor2 = Integer.parseInt(parts2[1]);
                return minor1 - minor2;
            }

            return 0;
        } catch (Exception e) {
            return 0; // If parsing fails, treat as equal
        }
    }
}