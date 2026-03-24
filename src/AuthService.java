import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AuthService - Handles user authentication and login.
 *
 * Demonstrates OOP concepts:
 *  - Abstraction: Hides SQL details from Main.java.
 *  - Encapsulation: Validates credentials in one place.
 */
public class AuthService {

    /**
     * Attempts to log in the user with the given username and password.
     *
     * @param username The entered username.
     * @param password The entered password.
     * @return A User object if credentials are valid, null otherwise.
     */
    public User login(String username, String password) {
        User loggedInUser = null;

        // SQL query uses PreparedStatement to prevent SQL injection
        String sql = "SELECT id, username, password, role FROM users " +
                     "WHERE username = ? AND password = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Credentials matched - build User object
                loggedInUser = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("[AUTH ERROR] Login failed: " + e.getMessage());
        }

        return loggedInUser;
    }

    /**
     * Registers a new user in the database.
     * Only admin users should be allowed to call this in the UI.
     *
     * @param username New username.
     * @param password New password (plain text for simplicity).
     * @param role     "Admin" or "StoreKeeper".
     * @return true if registration was successful, false otherwise.
     */
    public boolean register(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            // Likely a duplicate username
            System.out.println("[AUTH ERROR] Could not register user: " + e.getMessage());
            return false;
        }
    }
}
