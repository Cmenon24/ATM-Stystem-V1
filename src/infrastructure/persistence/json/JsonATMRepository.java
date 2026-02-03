package infrastructure.persistence.json;

import domain.core.ATM;
import infrastructure.persistence.interfaces.ATMRepository;

/**
 * JSON implementation of ATMRepository.
 * Stores ATM state in atm.json file.
 *
 * File: data/atm.json
 * Format: Single ATM object as JSON
 *
 * Stores: cash by denomination, ink level, paper level, software version
 */
public class JsonATMRepository implements ATMRepository {

    private static final String FILENAME = "atm.json";
    private final JsonFileHandler fileHandler;

    public JsonATMRepository(String dataDirectory) {
        this.fileHandler = new JsonFileHandler(dataDirectory);  // ← CORRECT
    }

    @Override
    public ATM load() {
        // Try to load from file first
        ATM savedATM = fileHandler.readFromFile(FILENAME, ATM.class);

        if (savedATM != null) {
            // File exists - restore singleton state
            ATM instance = ATM.getInstance();
            instance.setCashByDenomination(savedATM.getCashByDenomination());
            instance.setInkLevel(savedATM.getInkLevel());
            instance.setPaperLevel(savedATM.getPaperLevel());
            instance.setSoftwareVersion(savedATM.getSoftwareVersion());
            return instance;
        }

        // No file - return fresh singleton instance
        return ATM.getInstance();
    }

    @Override
    public void save(ATM atm) {
        fileHandler.writeToFile(FILENAME, atm);
    }
}