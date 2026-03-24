import java.util.Scanner;

/**
 * Main.java - Entry point for the Store Management System.
 *
 * Program Flow:
 *  1. Show Login Menu until user authenticates.
 *  2. After login, show Main Menu based on role.
 *  3. Navigate to Inventory or Sales module.
 *  4. Perform operations and return to menu.
 *  5. Exit gracefully by closing DB connection.
 *
 * OOP Concepts demonstrated:
 *  - Polymorphism: Different menus based on user role.
 *  - Separation of concerns: Logic delegated to service classes.
 */
public class Main {

    // Shared scanner for all console input across the application
    static Scanner scanner = new Scanner(System.in);

    // Service instances (shared across menu methods)
    static AuthService      authService      = new AuthService();
    static InventoryService inventoryService = new InventoryService();
    static SalesService     salesService     = new SalesService();

    // Currently logged-in user
    static User currentUser = null;

    // ================================================================
    //  PROGRAM ENTRY POINT
    // ================================================================
    public static void main(String[] args) {
        printBanner();

        // Keep showing login until successful
        while (currentUser == null) {
            showLoginMenu();
        }

        // Show main menu in a loop until user exits
        boolean running = true;
        while (running) {
            running = showMainMenu();
        }

        // Clean up resources
        DBConnection.closeConnection();
        System.out.println("\nThank you for using Store Management System. Goodbye!");
    }

    // ================================================================
    //  BANNER
    // ================================================================
    private static void printBanner() {
        System.out.println("\n=======================================================");
        System.out.println("       STORE MANAGEMENT SYSTEM");
        System.out.println("       Powered by Java + MySQL");
        System.out.println("=======================================================");
    }

    // ================================================================
    //  LOGIN MENU
    // ================================================================
    private static void showLoginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username : ");
        String username = scanner.nextLine().trim();

        System.out.print("Password : ");
        String password = scanner.nextLine().trim();

        currentUser = authService.login(username, password);

        if (currentUser != null) {
            System.out.println("\n[LOGIN SUCCESS] Welcome, " + currentUser.getUsername()
                               + "! Role: " + currentUser.getRole());
        } else {
            System.out.println("\n[LOGIN FAILED] Invalid username or password. Try again.");
        }
    }

    // ================================================================
    //  MAIN MENU (after login)
    //  Returns false when the user chooses to exit.
    // ================================================================
    private static boolean showMainMenu() {
        System.out.println("\n========================================");
        System.out.println("  MAIN MENU  [" + currentUser.getRole() + "]");
        System.out.println("========================================");
        System.out.println("  1. Inventory Management");
        System.out.println("  2. Sales Management");

        // Admin-only options
        if (currentUser.isAdmin()) {
            System.out.println("  3. Add New User");
        }

        System.out.println("  0. Exit");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");

        int choice = readInt();

        switch (choice) {
            case 1:
                showInventoryMenu();
                break;
            case 2:
                showSalesMenu();
                break;
            case 3:
                if (currentUser.isAdmin()) {
                    addUserMenu();
                } else {
                    System.out.println("[ACCESS DENIED] Only Admin can add users.");
                }
                break;
            case 0:
                return false; // Signal to exit main loop
            default:
                System.out.println("[ERROR] Invalid choice. Please enter a valid option.");
        }

        return true;
    }

    // ================================================================
    //  INVENTORY MANAGEMENT MENU
    // ================================================================
    private static void showInventoryMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- INVENTORY MANAGEMENT ---");
            System.out.println("  1. View All Products");
            System.out.println("  2. Add Product        [Admin Only]");
            System.out.println("  3. Update Product     [Admin Only]");
            System.out.println("  4. Delete Product     [Admin Only]");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    // Any role can view products
                    inventoryService.displayProducts();
                    break;

                case 2:
                    if (!currentUser.isAdmin()) {
                        System.out.println("[ACCESS DENIED] Only Admin can add products.");
                        break;
                    }
                    addProductMenu();
                    break;

                case 3:
                    if (!currentUser.isAdmin()) {
                        System.out.println("[ACCESS DENIED] Only Admin can update products.");
                        break;
                    }
                    updateProductMenu();
                    break;

                case 4:
                    if (!currentUser.isAdmin()) {
                        System.out.println("[ACCESS DENIED] Only Admin can delete products.");
                        break;
                    }
                    deleteProductMenu();
                    break;

                case 0:
                    back = true;
                    break;

                default:
                    System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    // ---- Add Product ------------------------------------------------
    private static void addProductMenu() {
        System.out.println("\n--- Add New Product ---");

        System.out.print("Product Name  : ");
        String name = scanner.nextLine().trim();

        System.out.print("Price (Rs.)   : ");
        double price = readDouble();

        System.out.print("Quantity      : ");
        int qty = readInt();

        System.out.print("Supplier Name : ");
        String supplier = scanner.nextLine().trim();

        Product product = new Product(name, price, qty, supplier);
        inventoryService.addProduct(product);
    }

    // ---- Update Product ---------------------------------------------
    private static void updateProductMenu() {
        System.out.println("\n--- Update Product ---");
        inventoryService.displayProducts();

        System.out.print("\nEnter Product ID to update: ");
        int id = readInt();

        Product existing = inventoryService.getProductById(id);
        if (existing == null) {
            System.out.println("[ERROR] Product not found.");
            return;
        }

        System.out.println("Current Name  : " + existing.getName());
        System.out.print("New Name (leave blank to keep): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = existing.getName();

        System.out.printf("Current Price : %.2f%n", existing.getPrice());
        System.out.print("New Price (0 to keep): ");
        double price = readDouble();
        if (price <= 0) price = existing.getPrice();

        System.out.println("Current Qty   : " + existing.getQuantity());
        System.out.print("New Quantity (-1 to keep): ");
        int qty = readInt();
        if (qty < 0) qty = existing.getQuantity();

        System.out.println("Current Supplier: " + existing.getSupplier());
        System.out.print("New Supplier (leave blank to keep): ");
        String supplier = scanner.nextLine().trim();
        if (supplier.isEmpty()) supplier = existing.getSupplier();

        Product updated = new Product(id, name, price, qty, supplier);
        inventoryService.updateProduct(updated);
    }

    // ---- Delete Product ---------------------------------------------
    private static void deleteProductMenu() {
        System.out.println("\n--- Delete Product ---");
        inventoryService.displayProducts();

        System.out.print("\nEnter Product ID to delete: ");
        int id = readInt();

        System.out.print("Are you sure? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if ("yes".equalsIgnoreCase(confirm)) {
            inventoryService.deleteProduct(id);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ================================================================
    //  SALES MANAGEMENT MENU
    // ================================================================
    private static void showSalesMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- SALES MANAGEMENT ---");
            System.out.println("  1. Create New Sale");
            System.out.println("  2. View Sales History");
            System.out.println("  3. Generate Sales Report");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    createSaleMenu();
                    break;
                case 2:
                    salesService.displaySalesHistory();
                    break;
                case 3:
                    salesService.generateSalesReport();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    // ---- Create Sale ------------------------------------------------
    private static void createSaleMenu() {
        System.out.println("\n--- Create New Sale ---");
        inventoryService.displayProducts();

        System.out.print("\nEnter Product ID to sell: ");
        int productId = readInt();

        System.out.print("Enter Quantity to sell  : ");
        int quantity = readInt();

        salesService.createSale(productId, quantity);
    }

    // ================================================================
    //  ADMIN: ADD USER MENU
    // ================================================================
    private static void addUserMenu() {
        System.out.println("\n--- Add New User (Admin Only) ---");

        System.out.print("New Username : ");
        String username = scanner.nextLine().trim();

        System.out.print("New Password : ");
        String password = scanner.nextLine().trim();

        System.out.println("Select Role:");
        System.out.println("  1. Admin");
        System.out.println("  2. StoreKeeper");
        System.out.print("Choice: ");
        int roleChoice = readInt();

        String role = (roleChoice == 1) ? "Admin" : "StoreKeeper";

        boolean success = authService.register(username, password, role);
        if (success) {
            System.out.println("[SUCCESS] User '" + username + "' created with role: " + role);
        } else {
            System.out.println("[ERROR] Could not create user. Username may already exist.");
        }
    }

    // ================================================================
    //  INPUT HELPER METHODS
    // ================================================================

    /**
     * Safely reads an integer from the console.
     * Returns -1 if input is not a valid integer.
     */
    private static int readInt() {
        try {
            String line = scanner.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Safely reads a double from the console.
     * Returns -1.0 if input is not a valid number.
     */
    private static double readDouble() {
        try {
            String line = scanner.nextLine().trim();
            return Double.parseDouble(line);
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }
}
