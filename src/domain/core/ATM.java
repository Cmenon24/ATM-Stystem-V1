package domain.core;

import java.util.HashMap;
import java.util.Map;

/**
 * SOLID Principles:
 * - Single Responsibility: Manages ATM resources (cash, ink, paper, software)
 * - Open/Closed: Can be extended with new resources without modification
 *
 * OOP Pillars:
 * - Encapsulation: Private resources with controlled access
 * - Abstraction: Hides resource management complexity
 *
 * Design Pattern: Singleton - Ensures only one ATM instance exists
 */

public class ATM {
    /**
     * Singleton instance of the ATM
     */
    private static ATM instance;
    private String atmId;

    /**
     * Cash available in ATM by denomination (key: denomination, value: count)
     */
    private Map<Integer, Integer> cashByDenomination;

    /**
     * Ink level as percentage (0-100)
     */
    private int inkLevel;

    /**
     * Paper level as sheet count
     */
    private int paperLevel;

    /**
     * Current software version will show like 1.0, 2.0 etc
     */
    private String softwareVersion;

    /**
     * Private constructor to prevent direct instantiation (Singleton pattern).
     * Initializes ATM with default values.
     */
    private ATM() {
        this.atmId = "ATM-001";
        this.cashByDenomination = new HashMap<>();
        initializeDefaultCash();
        this.inkLevel = 5;  // Start with low ink (5%)
        this.paperLevel = 6;  // Start with low paper (5 sheets)
        this.softwareVersion = "1.0";
    }

    /**
     * Gets the singleton instance of ATM.
     * Creates instance if it doesn't exist (lazy initialization).
     *
     * Design Pattern: Singleton - Thread-safe lazy initialization
     *
     * @return ATM instance
     */
    public static synchronized ATM getInstance() {
        if (instance == null) {
            instance = new ATM();
        }
        return instance;
    }

    /**
     * Resets the singleton instance used for testing*/
    public static synchronized void resetInstance() {
        instance = null;
    }

    /**
     * Initializes ATM with default cash distribution.
     * Total: €350
     * Distribution: 1x€100, 2x€50, 5x€20, 5x€10 = €350
     */
    private void initializeDefaultCash() {
        cashByDenomination.put(100, 1);   // 1x €100 = €100
        cashByDenomination.put(50, 2);    // 2x €50 = €100
        cashByDenomination.put(20, 5);    // 5x €20 = €100
        cashByDenomination.put(10, 5);    // 5x €10 = €50
    }

    // Getters and Setters
    public String getAtmId() {
        return atmId;
    }
    public void setAtmId(String atmId) {
        this.atmId = atmId;
    }

    /**
     * Gets cash distribution by denomination.
     *
     * @return Map of denomination to count
     */
    public Map<Integer, Integer> getCashByDenomination() {
        return new HashMap<>(cashByDenomination);  // Return copy for encapsulation
    }

    /**
     * Sets cash distribution by denomination.
     *
     * @param cashByDenomination Cash distribution map
     */
    public void setCashByDenomination(Map<Integer, Integer> cashByDenomination) {
        this.cashByDenomination = new HashMap<>(cashByDenomination);
    }

    /**
     * Gets ink level percentage.
     *
     * @return Ink level (0-100)
     */
    public int getInkLevel() {
        return inkLevel;
    }

    /**
     * Sets ink level percentage.
     *
     * @param inkLevel Ink level to set (0-100)
     */
    public void setInkLevel(int inkLevel) {
        this.inkLevel = Math.max(0, Math.min(100, inkLevel));  // Clamp to 0-100
    }

    /**
     * Gets paper level in sheets.
     *
     * @return Paper level which is sheet count
     */
    public int getPaperLevel() {
        return paperLevel;
    }

    /**
     * Sets paper level in sheets.
     *
     * @param paperLevel Paper level to set
     */
    public void setPaperLevel(int paperLevel) {
        this.paperLevel = Math.max(0, paperLevel);  // Ensure non-negative
    }

    /**
     * Gets software version.
     *
     * @return Software version string
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets software version.
     *
     * @param softwareVersion Version string to set
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    // Business Logic Methods

    /**
     * Calculates total cash available in ATM.
     *
     * @return Total cash in euros
     */
    public double getTotalCash() {
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cashByDenomination.entrySet()) {
            total += entry.getKey() * entry.getValue();
        }
        return total;
    }

    /**
     * Checks if ATM has sufficient cash for a withdrawal.
     *
     * @param amount Amount to check
     * @return true if sufficient cash available
     */
    public boolean hasSufficientCash(double amount) {
        return getTotalCash() >= amount;
    }

    /**
     * Checks if ATM can print a receipt (has ink and paper).
     *
     * @return true if can print
     */
    public boolean canPrintReceipt() {
        return inkLevel > 0 && paperLevel > 0;
    }

    /**
     * Uses ink and paper for printing a receipt.
     * Decreases ink by 1% and paper by 1 sheet.
     */
    public void useReceiptResources() {
        if (canPrintReceipt()) {
            inkLevel = Math.max(0, inkLevel - 1);
            paperLevel = Math.max(0, paperLevel - 1);
        }
    }

    /**
     * Adds cash to ATM (used by technician for replenishment).
     *
     * @param denomination Denomination to add (10, 20, 50, 100)
     * @param count Number of notes to add
     */
    public void addCash(int denomination, int count) {
        cashByDenomination.put(denomination,
                cashByDenomination.getOrDefault(denomination, 0) + count);
    }

    /**
     * Removes cash from ATM (used when dispensing to customer).
     *
     * @param denomination Denomination to remove
     * @param count Number of notes to remove
     * @return true if successful, false if insufficient notes
     */
    public boolean removeCash(int denomination, int count) {
        int current = cashByDenomination.getOrDefault(denomination, 0);
        if (current >= count) {
            cashByDenomination.put(denomination, current - count);
            return true;
        }
        return false;
    }

    /**
     * Refills ink to 100% (used by technician).
     */
    public void refillInk() {
        this.inkLevel = 100;
    }

    /**
     * Restocks paper (used by technician).
     *
     * @param sheets Number of sheets to add
     */
    public void restockPaper(int sheets) {
        this.paperLevel += sheets;
    }

    /**
     * Gets ATM status as formatted string.
     *
     * @return Formatted status string
     */
    public String getStatusReport() {
        StringBuilder status = new StringBuilder();
        status.append("================================\n");
        status.append("       ATM STATUS REPORT        \n");
        status.append("================================\n");
        status.append("ATM ID: ").append(atmId).append("\n");
        status.append("Software Version: ").append(softwareVersion).append("\n");
        status.append("Total Cash: €").append(String.format("%.2f", getTotalCash())).append("\n");
        status.append("Cash by Denomination:\n");
        status.append("  €100 notes: ").append(cashByDenomination.getOrDefault(100, 0)).append("\n");
        status.append("  €50 notes: ").append(cashByDenomination.getOrDefault(50, 0)).append("\n");
        status.append("  €20 notes: ").append(cashByDenomination.getOrDefault(20, 0)).append("\n");
        status.append("  €10 notes: ").append(cashByDenomination.getOrDefault(10, 0)).append("\n");
        status.append("Ink Level: ").append(inkLevel).append("%\n");
        status.append("Paper Level: ").append(paperLevel).append(" sheets\n");
        status.append("================================\n");
        return status.toString();
    }

    @Override
    public String toString() {
        return "ATM{" +
                "atmId='" + atmId + '\'' +
                ", totalCash=" + String.format("%.2f", getTotalCash()) +
                ", inkLevel=" + inkLevel +
                ", paperLevel=" + paperLevel +
                ", softwareVersion='" + softwareVersion + '\'' +
                '}';
    }

    /**
     * Gets cash status report (for replenishment).*/

    public String getCashStatusReport() {
        StringBuilder status = new StringBuilder();
        status.append("================================\n");
        status.append("     ATM CASH STATUS REPORT     \n");
        status.append("================================\n");
        status.append("ATM ID: ").append(atmId).append("\n");
        status.append("Total Cash: €").append(String.format("%.2f", getTotalCash())).append("\n");
        status.append("Cash by Denomination:\n");
        status.append("  €100 notes: ").append(cashByDenomination.getOrDefault(100, 0)).append("\n");
        status.append("  €50 notes: ").append(cashByDenomination.getOrDefault(50, 0)).append("\n");
        status.append("  €20 notes: ").append(cashByDenomination.getOrDefault(20, 0)).append("\n");
        status.append("  €10 notes: ").append(cashByDenomination.getOrDefault(10, 0)).append("\n");
        status.append("================================\n");
        return status.toString();
    }

    /**
     * Gets ink status report (for refilling).
     * Shows: ATM ID, ink level
     */
    public String getInkStatusReport() {
        StringBuilder status = new StringBuilder();
        status.append("================================\n");
        status.append("     ATM INK STATUS REPORT      \n");
        status.append("================================\n");
        status.append("ATM ID: ").append(atmId).append("\n");
        status.append("Ink Level: ").append(inkLevel).append("%\n");
        status.append("================================\n");
        return status.toString();
    }

    /**
     * Gets paper status report (for restocking).
     * Shows: ATM ID, paper level
     */
    public String getPaperStatusReport() {
        StringBuilder status = new StringBuilder();
        status.append("================================\n");
        status.append("    ATM PAPER STATUS REPORT     \n");
        status.append("================================\n");
        status.append("ATM ID: ").append(atmId).append("\n");
        status.append("Paper Level: ").append(paperLevel).append(" sheets\n");
        status.append("================================\n");
        return status.toString();
    }

    /**
     * Gets software status report (for updates).
     * Shows: ATM ID, current software version
     */
    public String getSoftwareStatusReport() {
        StringBuilder status = new StringBuilder();
        status.append("================================\n");
        status.append("   ATM SOFTWARE STATUS REPORT   \n");
        status.append("================================\n");
        status.append("ATM ID: ").append(atmId).append("\n");
        status.append("Current Software Version: ").append(softwareVersion).append("\n");
        status.append("================================\n");
        return status.toString();
    }
}