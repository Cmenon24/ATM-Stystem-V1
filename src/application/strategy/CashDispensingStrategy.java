package application.strategy;

import java.util.Map;

/**
 * Strategy interface for cash dispensing algorithms.
 * Strategy Pattern: Allows different algorithms for optimal note distribution.
 *
 * Example: Customer withdraws €230
 * - Strategy calculates: 2x€100, 1x€20, 1x€10
 * - Result: Map<denomination, count> = {100: 2, 20: 1, 10: 1}
 */
public interface CashDispensingStrategy {

    /**
     * Calculates optimal note distribution for withdrawal amount.
     *
     * @param amount Withdrawal amount (must be multiple of €10)
     * @param availableCash Map of available notes in ATM (denomination -> count)
     * @return Map of notes to dispense (denomination -> count), or null if cannot dispense exact amount
     *
     * Example:
     * - Input: amount=230, availableCash={100: 5, 50: 10, 20: 20, 10: 30}
     * - Output: {100: 2, 20: 1, 10: 1}
     */
    Map<Integer, Integer> dispense(double amount, Map<Integer, Integer> availableCash);
}