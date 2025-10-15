import java.sql.*;

public class DBConnection {
    // MySQL configuration with multiple connection string options
    private static final String DB_NAME = "inventorydb";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAMPP password is empty
    
    // Try multiple connection URLs for different MySQL configurations
    private static final String[] CONNECTION_URLS = {
        // Primary: Modern MySQL connector with all parameters
        "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
        // Fallback 1: Without timezone specification
        "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true",
        // Fallback 2: Minimal parameters
        "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false",
        // Fallback 3: Legacy compatibility mode
        "jdbc:mysql://localhost:3306/" + DB_NAME
    };

    public static Connection getConnection() {
        // Try to load the driver first
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC driver not found!");
            System.err.println("📁 Please ensure mysql-connector-j-8.0.33.jar is in the lib folder");
            System.err.println("🔧 Current classpath: " + System.getProperty("java.class.path"));
            return null;
        }
        
        // Try each connection URL
        Connection conn = null;
        SQLException lastException = null;
        
        for (int i = 0; i < CONNECTION_URLS.length; i++) {
            try {
                conn = DriverManager.getConnection(CONNECTION_URLS[i], USER, PASSWORD);
                if (conn != null && !conn.isClosed()) {
                    System.out.println("✓ Database connected successfully! (Method " + (i + 1) + ")");
                    return conn;
                }
            } catch (SQLException e) {
                lastException = e;
                // Continue to next connection method
            }
        }
        
        // If all methods failed, provide detailed error information
        if (lastException != null) {
            System.err.println("❌ Database connection failed after trying all methods!");
            System.err.println("📋 Error Details:");
            System.err.println("   - Error Code: " + lastException.getErrorCode());
            System.err.println("   - SQL State: " + lastException.getSQLState());
            System.err.println("   - Message: " + lastException.getMessage());
            System.err.println("");
            System.err.println("🔍 Troubleshooting Steps:");
            System.err.println("   1. Start XAMPP Control Panel");
            System.err.println("   2. Click 'Start' button for MySQL");
            System.err.println("   3. Check if MySQL port 3306 is not blocked");
            System.err.println("   4. Import db/inventory.sql into phpMyAdmin");
            System.err.println("   5. Verify database name is 'inventorydb'");
            System.err.println("   6. Check if MySQL root password is empty (default)");
        }
        
        return null;
    }
    
    public static void testConnection() {
        System.out.println("🔍 Testing database connection...");
        System.out.println("📍 Attempting to connect to: localhost:3306/" + DB_NAME);
        System.out.println("👤 Username: " + USER);
        System.out.println("");
        
        Connection conn = getConnection();
        if (conn != null) {
            try {
                // Test if database exists
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT DATABASE(), VERSION()");
                if (rs.next()) {
                    System.out.println("✅ Connected to database: " + rs.getString(1));
                    System.out.println("📦 MySQL version: " + rs.getString(2));
                }
                
                // Test if main tables exist
                DatabaseMetaData meta = conn.getMetaData();
                String[] tableNames = {"product", "customer", "supplier", "transaction"};
                int foundTables = 0;
                
                System.out.println("");
                System.out.println("🔎 Checking tables:");
                for (String tableName : tableNames) {
                    ResultSet tables = meta.getTables(DB_NAME, null, tableName, null);
                    if (tables.next()) {
                        System.out.println("   ✓ " + tableName + " - Found");
                        foundTables++;
                    } else {
                        System.out.println("   ✗ " + tableName + " - Missing");
                    }
                    tables.close();
                }
                
                System.out.println("");
                if (foundTables == tableNames.length) {
                    // Check if tables have data
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM product");
                    if (rs.next()) {
                        System.out.println("✅ System fully ready! Found " + rs.getInt(1) + " products.");
                    }
                } else {
                    System.out.println("⚠️ Some tables are missing!");
                    System.out.println("📝 Action required: Import db/inventory.sql");
                    System.out.println("   - Open XAMPP phpMyAdmin (http://localhost/phpmyadmin)");
                    System.out.println("   - Create database 'inventorydb' if not exists");
                    System.out.println("   - Import the db/inventory.sql file");
                }
                
                conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error checking database structure:");
                System.err.println("   " + e.getMessage());
                System.err.println("");
                System.err.println("💡 This usually means the database exists but is empty.");
                System.err.println("   Please import db/inventory.sql file.");
            }
        } else {
            System.out.println("❌ Connection failed! MySQL may not be running.");
            System.out.println("");
            System.out.println("🔧 Quick Fix:");
            System.out.println("   1. Open XAMPP Control Panel");
            System.out.println("   2. Start the MySQL service");
            System.out.println("   3. Wait for the green 'Running' indicator");
            System.out.println("   4. Try running this test again");
        }
    }
    
    public static boolean isDatabaseReady() {
        try (Connection conn = getConnection()) {
            if (conn == null) return false;
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM product");
            return rs.next(); // If we can query products table, we're ready
            
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Creates the database and imports schema if it doesn't exist
     * Useful for automated setup
     */
    public static boolean createDatabaseIfNotExists() {
        try {
            // First try to connect without specifying database
            String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true";
            Connection conn = DriverManager.getConnection(baseUrl, USER, PASSWORD);
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("✓ Database '" + DB_NAME + "' is ready");
            
            conn.close();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Failed to create database: " + e.getMessage());
            return false;
        }
    }
    
    // Main method for testing database connection from command line
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("    DATABASE CONNECTION TEST");
        System.out.println("===========================================");
        testConnection();
        System.out.println("===========================================");
    }
}