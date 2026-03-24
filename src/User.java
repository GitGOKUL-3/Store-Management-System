/**
 * User - Model class representing a user of the system.
 *
 * Demonstrates OOP concepts:
 *  - Encapsulation: All fields are private, accessed via getters/setters.
 *  - Abstraction: Exposes only what is needed externally.
 */
public class User {

    // Private fields (Encapsulation)
    private int    id;
    private String username;
    private String password;
    private String role;      // "Admin" or "StoreKeeper"

    // ---- Constructor ----
    public User(int id, String username, String password, String role) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    // ---- Getters ----
    public int    getId()       { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    // ---- Setters ----
    public void setId(int id)             { this.id       = id; }
    public void setUsername(String u)     { this.username = u; }
    public void setPassword(String p)     { this.password = p; }
    public void setRole(String role)      { this.role     = role; }

    /**
     * Helper: Check if user has Admin role.
     */
    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
