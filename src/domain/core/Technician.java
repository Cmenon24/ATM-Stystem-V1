package domain.core;

/**
 * Extends User and adds technician-specific functionality.
 *
 * SOLID Principles:
 * - Single Responsibility: Manages technician data and maintenance permissions
 * - Liskov Substitution: Can be used wherever User is expected
 *
 * OOP Pillars:
 * - Inheritance: Extends User class
 * - Encapsulation: Private technician ID with controlled access
 * - Polymorphism: Implements displayMenu() with technician-specific behavior
 */
public class Technician extends User {
    private String technicianId;

    /**
     * Constructor for creating a new technician.
     */
    public Technician(String userId, String name, String pin, String technicianId) {
        super(userId, name, pin, Role.TECHNICIAN);
        this.technicianId = technicianId;
    }

    /**
     * Default constructor for JSON deserialization.
     */
    public Technician() {
        super();
        this.setRole(Role.TECHNICIAN);
    }

    /**
     * Gets the technician's employee ID.
     */
    public String getTechnicianId() {
        return technicianId;
    }

    /**
     * Sets the technician's employee ID.
     */
    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    /**
     * Displays technician-specific menu options.
     * This method will be called by the presentation layer.
     *
     * OOP Pillar: Polymorphism - Technician-specific implementation
     * Note: Actual menu display logic is in presentation layer (TechnicianMenu.java)
     */
    @Override
    public void displayMenu() {
        // Implementation delegated to presentation layer (TechnicianMenu)
        // This method exists to satisfy the abstract contract from User
        System.out.println("Technician Menu:");
        System.out.println("1. View ATM Status");
        System.out.println("2. Replenish Cash");
        System.out.println("3. Refill Ink");
        System.out.println("4. Restock Paper");
        System.out.println("5. Update Software");
        System.out.println("6. Exit");
    }

    @Override
    public String toString() {
        return "Technician{" +
                "userId='" + getUserId() + '\'' +
                ", name='" + getName() + '\'' +
                ", technicianId='" + technicianId + '\'' +
                '}';
    }
}