package infrastructure.persistence.interfaces;

import domain.core.ATM;

/**
 * Repository interface for ATM data persistence.
 *
 * SOLID Principles:
 * - Single Responsibility: Only ATM state persistence
 * - Dependency Inversion: MaintenanceService depends on this interface
 */
public interface ATMRepository {

    /**
     * Loads ATM state from storage.
     * Restores ATM cash, ink, paper, software version.
     *
     * @return ATM instance with loaded state
     */
    ATM load();

    /**
     * Saves current ATM state to storage.
     * Persists cash levels, ink, paper, software version.
     * Called after any maintenance operation or transaction.
     *
     * @param atm ATM instance to save
     */
    void save(ATM atm);
}