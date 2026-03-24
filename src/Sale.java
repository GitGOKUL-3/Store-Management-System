import java.util.Date;

/**
 * Sale - Model class representing a sale transaction.
 *
 * Demonstrates OOP concepts:
 *  - Encapsulation: Private fields with getters/setters.
 */
public class Sale {

    private int    id;
    private int    productId;
    private String productName; // Derived from JOIN, not stored in DB
    private int    quantity;
    private double total;
    private Date   date;

    // ---- Constructor for creating a new sale record ----
    public Sale(int productId, int quantity, double total) {
        this.productId = productId;
        this.quantity  = quantity;
        this.total     = total;
    }

    // ---- Constructor for sales retrieved from the database ----
    public Sale(int id, int productId, String productName, int quantity, double total, Date date) {
        this.id          = id;
        this.productId   = productId;
        this.productName = productName;
        this.quantity    = quantity;
        this.total       = total;
        this.date        = date;
    }

    // ---- Getters ----
    public int    getId()          { return id; }
    public int    getProductId()   { return productId; }
    public String getProductName() { return productName; }
    public int    getQuantity()    { return quantity; }
    public double getTotal()       { return total; }
    public Date   getDate()        { return date; }

    // ---- Setters ----
    public void setId(int id)                  { this.id          = id; }
    public void setProductId(int pid)          { this.productId   = pid; }
    public void setProductName(String pname)   { this.productName = pname; }
    public void setQuantity(int qty)           { this.quantity    = qty; }
    public void setTotal(double total)         { this.total       = total; }
    public void setDate(Date date)             { this.date        = date; }

    @Override
    public String toString() {
        return String.format("Sale{id=%d, product='%s', qty=%d, total=%.2f, date=%s}",
                id, productName, quantity, total, date);
    }
}
