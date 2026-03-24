/**
 * Product - Model class representing a product in the store inventory.
 *
 * Demonstrates OOP concepts:
 *  - Encapsulation: All fields are private with getters/setters.
 */
public class Product {

    private int    id;
    private String name;
    private double price;
    private int    quantity;
    private String supplier;

    // ---- Constructor for creating a new product (no ID yet) ----
    public Product(String name, double price, int quantity, String supplier) {
        this.name     = name;
        this.price    = price;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    // ---- Constructor for products retrieved from the database ----
    public Product(int id, String name, double price, int quantity, String supplier) {
        this.id       = id;
        this.name     = name;
        this.price    = price;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    // ---- Getters ----
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public double getPrice()    { return price; }
    public int    getQuantity() { return quantity; }
    public String getSupplier() { return supplier; }

    // ---- Setters ----
    public void setId(int id)              { this.id       = id; }
    public void setName(String name)       { this.name     = name; }
    public void setPrice(double price)     { this.price    = price; }
    public void setQuantity(int qty)       { this.quantity = qty; }
    public void setSupplier(String s)      { this.supplier = s; }

    /**
     * Returns true if the product is currently in stock.
     */
    public boolean isInStock() {
        return this.quantity > 0;
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.2f, qty=%d, supplier='%s'}",
                id, name, price, quantity, supplier);
    }
}
