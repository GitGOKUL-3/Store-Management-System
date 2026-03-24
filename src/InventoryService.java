import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * InventoryService - Provides CRUD operations for the products table.
 *
 * Demonstrates OOP concepts:
 *  - Abstraction: Hides SQL from the calling code.
 *  - Modularity: All inventory logic is isolated in this service.
 */
public class InventoryService {

    // ---------------------------------------------------------------
    // ADD PRODUCT
    // ---------------------------------------------------------------
    /**
     * Adds a new product to the inventory.
     *
     * @param product Product object to insert.
     * @return true if insertion was successful.
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, price, quantity, supplier) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3,    product.getQuantity());
            stmt.setString(4, product.getSupplier());

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                System.out.println("\n[SUCCESS] Product '" + product.getName() + "' added successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("[INVENTORY ERROR] Could not add product: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // UPDATE PRODUCT
    // ---------------------------------------------------------------
    /**
     * Updates an existing product's details by product ID.
     *
     * @param product Product object with updated values (id must be set).
     * @return true if update was successful.
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, price=?, quantity=?, supplier=? WHERE id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3,    product.getQuantity());
            stmt.setString(4, product.getSupplier());
            stmt.setInt(5,    product.getId());

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                System.out.println("\n[SUCCESS] Product ID " + product.getId() + " updated successfully!");
                return true;
            } else {
                System.out.println("\n[WARNING] No product found with ID " + product.getId());
            }

        } catch (SQLException e) {
            System.out.println("[INVENTORY ERROR] Could not update product: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // DELETE PRODUCT
    // ---------------------------------------------------------------
    /**
     * Deletes a product from the inventory by its ID.
     *
     * @param productId The ID of the product to delete.
     * @return true if deletion was successful.
     */
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, productId);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                System.out.println("\n[SUCCESS] Product ID " + productId + " deleted successfully!");
                return true;
            } else {
                System.out.println("\n[WARNING] No product found with ID " + productId);
            }

        } catch (SQLException e) {
            System.out.println("[INVENTORY ERROR] Could not delete product: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // VIEW ALL PRODUCTS
    // ---------------------------------------------------------------
    /**
     * Retrieves all products from the database.
     *
     * @return List of Product objects; empty list if none found.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price, quantity, supplier FROM products ORDER BY id";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("supplier")
                );
                products.add(p);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("[INVENTORY ERROR] Could not fetch products: " + e.getMessage());
        }

        return products;
    }

    // ---------------------------------------------------------------
    // GET PRODUCT BY ID
    // ---------------------------------------------------------------
    /**
     * Fetches a single product by its ID.
     *
     * @param productId The product's database ID.
     * @return Product object, or null if not found.
     */
    public Product getProductById(int productId) {
        String sql = "SELECT id, name, price, quantity, supplier FROM products WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("supplier")
                );
                rs.close();
                stmt.close();
                return p;
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("[INVENTORY ERROR] Could not fetch product: " + e.getMessage());
        }

        return null;
    }

    // ---------------------------------------------------------------
    // REDUCE STOCK (used internally by SalesService)
    // ---------------------------------------------------------------
    /**
     * Reduces the stock quantity of a product after a sale.
     *
     * @param productId The product whose stock is to be reduced.
     * @param quantity  The amount to deduct.
     * @return true if the update succeeded.
     */
    public boolean reduceStock(int productId, int quantity) {
        String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity); // Safety guard: do not go negative

            int rows = stmt.executeUpdate();
            stmt.close();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("[INVENTORY ERROR] Could not reduce stock: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    // DISPLAY PRODUCTS (formatted table output)
    // ---------------------------------------------------------------
    /**
     * Prints all products in a formatted table to the console.
     */
    public void displayProducts() {
        List<Product> products = getAllProducts();

        if (products.isEmpty()) {
            System.out.println("\n  No products found in inventory.");
            return;
        }

        System.out.println("\n========================================================================");
        System.out.printf("  %-5s %-25s %-10s %-8s %-20s%n",
                          "ID", "Name", "Price", "Qty", "Supplier");
        System.out.println("========================================================================");

        for (Product p : products) {
            // Flag low-stock items
            String stockFlag = (p.getQuantity() <= 5) ? " [LOW]" : "";
            System.out.printf("  %-5d %-25s %-10.2f %-8d %-20s%s%n",
                              p.getId(), p.getName(), p.getPrice(),
                              p.getQuantity(), p.getSupplier(), stockFlag);
        }

        System.out.println("========================================================================");
        System.out.println("  Total products: " + products.size());
    }
}
