import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventorydb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            return null;
        }
    }
    
    public static void testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("✓ Database connection successful!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("✗ Database connection failed!");
        }
    }
}