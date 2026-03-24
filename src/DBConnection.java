import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton class to manage MySQL database connection.
 *
 * Provides a single shared Connection object throughout the application.
 * Change DB_URL, DB_USER, and DB_PASSWORD to match your MySQL setup.
 */
public class DBConnection {

    // ---- Database credentials (update these as needed) ----
    private static final String DB_URL = "jdbc:mysql://localhost:3306/store_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Gokul@123"; // <-- CHANGE THIS!

    // Single shared connection instance (Singleton pattern)
    private static Connection connection = null;

    /**
     * Returns the shared database connection.
     * Creates it on first call, reuses it afterward.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DB] Connected to database successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("[DB ERROR] MySQL JDBC Driver not found.");
            System.out.println("Download mysql-connector-java.jar and add it to the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] Unable to connect to database.");
            System.out.println("Check DB_URL, DB_USER, and DB_PASSWORD in DBConnection.java");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the shared database connection gracefully.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] Error while closing connection.");
            e.printStackTrace();
        }
    }
}
