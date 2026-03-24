import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SalesService - Manages sales transactions.
 *
 * Responsibilities:
 *  - Create a sale (validates stock, inserts record, deducts stock)
 *  - View sales history
 *  - Generate a simple sales report
 *
 * Demonstrates OOP:
 *  - Collaboration between services (uses InventoryService).
 *  - Abstraction: Hides complex transaction logic.
 */
public class SalesService {

    // Collaborating service for stock management
    private InventoryService inventoryService;

    public SalesService() {
        this.inventoryService = new InventoryService();
    }

    // ---------------------------------------------------------------
    // CREATE SALE
    // ---------------------------------------------------------------
    /**
     * Processes a sale for a given product and quantity.
     *
     * Steps:
     *  1. Validate product exists and has enough stock.
     *  2. Calculate total price.
     *  3. Insert sale record into the database.
     *  4. Reduce product stock via InventoryService.
     *
     * @param productId The product being sold.
     * @param quantity  The number of units to sell.
     * @return true if the sale was completed successfully.
     */
    public boolean createSale(int productId, int quantity) {

        // Step 1: Fetch product details
        Product product = inventoryService.getProductById(productId);

        if (product == null) {
            System.out.println("\n[SALES ERROR] Product with ID " + productId + " does not exist.");
            return false;
        }

        if (!product.isInStock()) {
            System.out.println("\n[SALES ERROR] Product '" + product.getName() + "' is out of stock.");
            return false;
        }

        if (product.getQuantity() < quantity) {
            System.out.println("\n[SALES ERROR] Insufficient stock!");
            System.out.println("  Available: " + product.getQuantity() + " units");
            System.out.println("  Requested: " + quantity + " units");
            return false;
        }

        // Step 2: Calculate total
        double total = product.getPrice() * quantity;

        // Step 3: Insert sale record
        String sql = "INSERT INTO sales (product_id, quantity, total, date) VALUES (?, ?, ?, NOW())";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1,    productId);
            stmt.setInt(2,    quantity);
            stmt.setDouble(3, total);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows == 0) {
                System.out.println("[SALES ERROR] Failed to record the sale in the database.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("[SALES ERROR] Could not create sale: " + e.getMessage());
            return false;
        }

        // Step 4: Reduce stock
        boolean stockReduced = inventoryService.reduceStock(productId, quantity);

        if (stockReduced) {
            System.out.println("\n[SUCCESS] Sale completed!");
            System.out.println("  Product : " + product.getName());
            System.out.println("  Quantity: " + quantity + " units");
            System.out.printf( "  Total   : Rs. %.2f%n", total);
            return true;
        } else {
            System.out.println("[SALES ERROR] Sale recorded but stock update failed. Check manually.");
            return false;
        }
    }

    // ---------------------------------------------------------------
    // SALES HISTORY
    // ---------------------------------------------------------------
    /**
     * Retrieves all past sales with product names (via JOIN).
     *
     * @return List of Sale objects.
     */
    public List<Sale> getSalesHistory() {
        List<Sale> sales = new ArrayList<>();

        // JOIN query to include product name
        String sql = "SELECT s.id, s.product_id, p.name AS product_name, " +
                     "s.quantity, s.total, s.date " +
                     "FROM sales s " +
                     "JOIN products p ON s.product_id = p.id " +
                     "ORDER BY s.date DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("total"),
                    rs.getTimestamp("date")
                );
                sales.add(sale);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("[SALES ERROR] Could not fetch sales history: " + e.getMessage());
        }

        return sales;
    }

    // ---------------------------------------------------------------
    // DISPLAY SALES HISTORY (formatted)
    // ---------------------------------------------------------------
    /**
     * Prints sales history in a formatted table to the console.
     */
    public void displaySalesHistory() {
        List<Sale> sales = getSalesHistory();

        if (sales.isEmpty()) {
            System.out.println("\n  No sales transactions found.");
            return;
        }

        System.out.println("\n================================================================================");
        System.out.printf("  %-5s %-22s %-8s %-12s %-20s%n",
                          "ID", "Product Name", "Qty", "Total (Rs.)", "Date");
        System.out.println("================================================================================");

        for (Sale s : sales) {
            System.out.printf("  %-5d %-22s %-8d %-12.2f %-20s%n",
                              s.getId(), s.getProductName(), s.getQuantity(),
                              s.getTotal(), s.getDate());
        }

        System.out.println("================================================================================");
    }

    // ---------------------------------------------------------------
    // SALES REPORT
    // ---------------------------------------------------------------
    /**
     * Generates a simple aggregated sales report.
     *
     * Displays:
     *  - Total number of transactions.
     *  - Total revenue.
     *  - Per-product breakdown of quantity sold and revenue.
     */
    public void generateSalesReport() {
        System.out.println("\n============================================================");
        System.out.println("           SALES REPORT - Store Management System");
        System.out.println("============================================================");

        // --- Overall summary ---
        String summarySql = "SELECT COUNT(*) AS total_transactions, SUM(total) AS total_revenue FROM sales";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(summarySql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.printf("  Total Transactions : %d%n", rs.getInt("total_transactions"));
                System.out.printf("  Total Revenue      : Rs. %.2f%n", rs.getDouble("total_revenue"));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("[REPORT ERROR] " + e.getMessage());
            return;
        }

        // --- Per-product breakdown ---
        System.out.println("\n  --- Per-Product Breakdown ---");
        System.out.printf("  %-25s %-12s %-15s%n", "Product", "Qty Sold", "Revenue (Rs.)");
        System.out.println("  -------------------------------------------------------");

        String breakdownSql =
            "SELECT p.name, SUM(s.quantity) AS total_qty, SUM(s.total) AS total_rev " +
            "FROM sales s " +
            "JOIN products p ON s.product_id = p.id " +
            "GROUP BY p.name " +
            "ORDER BY total_rev DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(breakdownSql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.printf("  %-25s %-12d %.2f%n",
                    rs.getString("name"),
                    rs.getInt("total_qty"),
                    rs.getDouble("total_rev"));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("[REPORT ERROR] " + e.getMessage());
        }

        System.out.println("============================================================");
    }
}
