package application.service;

import domain.core.ATM;
import infrastructure.persistence.interfaces.ATMRepository;

import java.util.Map;

/**
 * Abstract base class for ATM maintenance operations.
 * Defines maintenance contract for all versions.
 *
 * Design Pattern: Template Method - defines skeleton, subclasses implement details
 *
 *
 * SOLID Principles:
 * - Open/Closed: Open for extension (V1, V2), closed for modification
 * - Liskov Substitution: V1 and V2 can replace this base class
 */
public abstract class MaintenanceService {

    protected final ATMRepository atmRepository;
    protected final ATM atm;

    /**
     * Constructor with dependency injection.
     *
     * @param atmRepository Repository for ATM data persistence
     */
    public MaintenanceService(ATMRepository atmRepository) {
        this.atmRepository = atmRepository;
        this.atm = ATM.getInstance();
    }

    /**
     * Gets current ATM status.
     * Available in ALL versions (read-only operation).
     *
     * @return Formatted ATM status report
     */
    public String getATMStatus() {
        return atm.getStatusReport();
    }
    /**Checks if maintenance operations are allowed in this version.
     * V1: Returns false (read-only)
     * V2: Returns true (full maintenance)
     /*/
    public abstract boolean isMaintenanceAllowed();

    /**
     * Replenishes ATM cash by denomination.
     * Implementation varies by version:
     * - V1: Throws UnauthorizedOperationException (not permitted)
     * - V2: Actually replenishes cash (full access)
     *
     * @param denominations Map of denomination -> count to add
     */
    public abstract void replenishCash(Map<Integer, Integer> denominations);

    /**
     * Refills ink to 100%.
     * Implementation varies by version.
     */
    public abstract void refillInk();

    /**
     * Restocks paper.
     * Implementation varies by version.
     *
     * @param sheets Number of sheets to add
     */
    public abstract void restockPaper(int sheets);

    /**
     * Updates software version.
     * Implementation varies by version.
     *
     * @param newVersion New software version string (e.g., "2.0")
     */
    public abstract void updateSoftware(String newVersion);
}
