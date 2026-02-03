package application.service;

import domain.exception.UnauthorizedOperationException;
import infrastructure.persistence.interfaces.ATMRepository;

import java.util.Map;

/**
 * Version 1 Maintenance Service - READ-ONLY ACCESS.
 * Technician can only view ATM status, cannot perform maintenance.
 *
 * This represents the BASE implementation (Version 1).
 * Version 2 will EXTEND this without MODIFYING it (Open/Closed Principle).
 *
 * Bank Actor: NOT involved (all operations are local to ATM)
 *
 * User Story: US-T2
 * - Technician in V1 can view ATM status
 * - Technician in V1 CANNOT perform maintenance (all methods throw exception)
 */
public class MaintenanceServiceV1 extends MaintenanceService {

    /**
     * Constructor with dependency injection.
     *
     * @param atmRepository Repository for ATM data
     */
    public MaintenanceServiceV1(ATMRepository atmRepository) {
        super(atmRepository);
    }
    @Override
    public boolean isMaintenanceAllowed() {
        return false;  // Not allowed in Version 1
    }
    /**
     * Version 1 restriction: Cannot replenish cash.
     *
     * @throws UnauthorizedOperationException Always throws - not permitted in V1
     */
    @Override
    public void replenishCash(Map<Integer, Integer> denominations) {
        throw new UnauthorizedOperationException(
                "Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities."
        );
    }

    /**
     * Version 1 restriction: Cannot refill ink.
     *
     * @throws UnauthorizedOperationException Always throws - not permitted in V1
     */
    @Override
    public void refillInk() {
        throw new UnauthorizedOperationException(
                "Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities."
        );
    }

    /**
     * Version 1 restriction: Cannot restock paper.
     *
     * @throws UnauthorizedOperationException Always throws - not permitted in V1
     */
    @Override
    public void restockPaper(int sheets) {
        throw new UnauthorizedOperationException(
                "Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities."
        );
    }

    /**
     * Version 1 restriction: Cannot update software.
     *
     * @throws UnauthorizedOperationException Always throws - not permitted in V1
     */
    @Override
    public void updateSoftware(String newVersion) {
        throw new UnauthorizedOperationException(
                "Operation not permitted in Version 1. Please upgrade to Version 2 for full maintenance capabilities."
        );
    }
}
