package application.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Optimal cash dispensing using greedy algorithm.
 * Prefers largest denominations first to minimize number of notes.
 *
 * Algorithm:
 * 1. Start with largest denomination (€100)
 * 2. Dispense as many as possible without exceeding amount
 * 3. Move to next denomination (€50, €20, €10)
 * 4. If exact amount can't be made, return null
 *
 * Example: €230
 * - Try €100: 230/100 = 2 notes, remaining = €30
 * - Try €50: 30/50 = 0 notes, remaining = €30
 * - Try €20: 30/20 = 1 note, remaining = €10
 * - Try €10: 10/10 = 1 note, remaining = €0
 * - Result: {100: 2, 20: 1, 10: 1}
 */
public class OptimalCashDispensingStrategy implements CashDispensingStrategy {

    // Available denominations in descending order
    private static final int[] DENOMINATIONS = {100, 50, 20, 10};

    @Override
    public Map<Integer, Integer> dispense(double amount, Map<Integer, Integer> availableCash) {
        // Validate amount is multiple of 10
        if (amount % 10 != 0) {
            return null; // Cannot dispense non-multiple of €10
        }

        Map<Integer, Integer> result = new HashMap<>();
        double remaining = amount;

        // Try each denomination from largest to smallest
        for (int denomination : DENOMINATIONS) {
            // How many of this denomination are available in ATM?
            int available = availableCash.getOrDefault(denomination, 0);

            // How many do we need for remaining amount?
            int needed = (int) (remaining / denomination);

            // Dispense the minimum of what we need and what's available
            int toDispense = Math.min(needed, available);

            if (toDispense > 0) {
                result.put(denomination, toDispense);
                remaining -= toDispense * denomination;
            }
        }

        // If we couldn't dispense exact amount, return null (cannot fulfill request)
        return remaining == 0 ? result : null;
    }
}