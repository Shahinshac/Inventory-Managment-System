import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel productTableModel, customerTableModel, supplierTableModel, transactionTableModel;
    private JTable productTable, customerTable, supplierTable, transactionTable;
    private JLabel statsProducts, statsCustomers, statsSuppliers, statsTransactions;

    public InventoryGUI() {
        setTitle("📦 Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Test database connection
        testDatabaseConnection();
        
        initComponents();
        loadAllData();
    }

    private void testDatabaseConnection() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, 
                "❌ Cannot connect to database!\n\n" +
                "Please ensure:\n" +
                "1. MySQL is running (XAMPP)\n" +
                "2. Database 'inventorydb' exists\n" +
                "3. Import db/inventory.sql\n" +
                "4. MySQL JDBC driver is in lib folder", 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } else {
            try {
                conn.close();
                System.out.println("✓ Database connected successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(0, 60));
        JLabel title = new JLabel("📦 Inventory Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        tabbedPane.addTab("📊 Dashboard", createDashboard());
        tabbedPane.addTab("📦 Products", createProductPanel());
        tabbedPane.addTab("👥 Customers", createCustomerPanel());
        tabbedPane.addTab("🚛 Suppliers", createSupplierPanel());
        tabbedPane.addTab("💳 Transactions", createTransactionPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        
        statsProducts = new JLabel("0", SwingConstants.CENTER);
        statsCustomers = new JLabel("0", SwingConstants.CENTER);
        statsSuppliers = new JLabel("0", SwingConstants.CENTER);
        statsTransactions = new JLabel("0", SwingConstants.CENTER);
        
        statsPanel.add(createStatCard("Products", statsProducts, new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Customers", statsCustomers, new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Suppliers", statsSuppliers, new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Transactions", statsTransactions, new Color(243, 156, 18)));
        
        panel.add(statsPanel, BorderLayout.NORTH);

        // Recent transactions
        String[] columns = {"ID", "Product", "Customer", "Quantity", "Date"};
        transactionTableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createRaisedBevelBorder());
        card.setPreferredSize(new Dimension(150, 80));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Product"));
        
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(15);
        JTextField qtyField = new JTextField(5);
        JTextField priceField = new JTextField(8);
        JButton addBtn = new JButton("Add Product");
        addBtn.setBackground(new Color(52, 152, 219));
        addBtn.setForeground(Color.WHITE);

        formPanel.add(new JLabel("ID:")); formPanel.add(idField);
        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Qty:")); formPanel.add(qtyField);
        formPanel.add(new JLabel("Price:")); formPanel.add(priceField);
        formPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                int qty = Integer.parseInt(qtyField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty!");
                    return;
                }

                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO product (id, name, quantity, price) VALUES (?, ?, ?, ?)");
                stmt.setInt(1, id);
                stmt.setString(2, name);
                stmt.setInt(3, qty);
                stmt.setDouble(4, price);

                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Product added successfully!");
                    idField.setText(""); nameField.setText(""); qtyField.setText(""); priceField.setText("");
                    loadProductData();
                    updateStats();
                }
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Quantity", "Price", "Status"};
        productTableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Product List"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Customer"));
        
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(20);
        JButton addBtn = new JButton("Add Customer");
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);

        formPanel.add(new JLabel("ID:")); formPanel.add(idField);
        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Email:")); formPanel.add(emailField);
        formPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name and email cannot be empty!");
                    return;
                }

                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO customer (id, name, email) VALUES (?, ?, ?)");
                stmt.setInt(1, id);
                stmt.setString(2, name);
                stmt.setString(3, email);

                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Customer added successfully!");
                    idField.setText(""); nameField.setText(""); emailField.setText("");
                    loadCustomerData();
                    updateStats();
                }
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Email"};
        customerTableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customer List"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Supplier"));
        
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(15);
        JTextField contactField = new JTextField(20);
        JButton addBtn = new JButton("Add Supplier");
        addBtn.setBackground(new Color(231, 76, 60));
        addBtn.setForeground(Color.WHITE);

        formPanel.add(new JLabel("ID:")); formPanel.add(idField);
        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Contact:")); formPanel.add(contactField);
        formPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();

                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name and contact cannot be empty!");
                    return;
                }

                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO supplier (id, name, contact) VALUES (?, ?, ?)");
                stmt.setInt(1, id);
                stmt.setString(2, name);
                stmt.setString(3, contact);

                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Supplier added successfully!");
                    idField.setText(""); nameField.setText(""); contactField.setText("");
                    loadSupplierData();
                    updateStats();
                }
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Contact"};
        supplierTableModel = new DefaultTableModel(columns, 0);
        supplierTable = new JTable(supplierTableModel);
        supplierTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Supplier List"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Record New Transaction"));
        
        JComboBox<String> productCombo = new JComboBox<>();
        JComboBox<String> customerCombo = new JComboBox<>();
        JTextField qtyField = new JTextField(5);
        JButton recordBtn = new JButton("Record Transaction");
        recordBtn.setBackground(new Color(243, 156, 18));
        recordBtn.setForeground(Color.WHITE);

        loadComboBoxData(productCombo, customerCombo);

        formPanel.add(new JLabel("Product:")); formPanel.add(productCombo);
        formPanel.add(new JLabel("Customer:")); formPanel.add(customerCombo);
        formPanel.add(new JLabel("Qty:")); formPanel.add(qtyField);
        formPanel.add(recordBtn);

        recordBtn.addActionListener(e -> {
            try {
                String productItem = (String) productCombo.getSelectedItem();
                String customerItem = (String) customerCombo.getSelectedItem();
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (productItem == null || customerItem == null) {
                    JOptionPane.showMessageDialog(this, "Please select product and customer!");
                    return;
                }

                int productId = Integer.parseInt(productItem.split(" - ")[0]);
                int customerId = Integer.parseInt(customerItem.split(" - ")[0]);

                Connection conn = DBConnection.getConnection();
                
                // Check stock
                PreparedStatement checkStmt = conn.prepareStatement("SELECT quantity FROM product WHERE id = ?");
                checkStmt.setInt(1, productId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    int available = rs.getInt("quantity");
                    if (available < qty) {
                        JOptionPane.showMessageDialog(this, "❌ Insufficient stock! Available: " + available);
                        return;
                    }

                    // Record transaction
                    PreparedStatement transStmt = conn.prepareStatement("INSERT INTO transaction (product_id, customer_id, quantity) VALUES (?, ?, ?)");
                    transStmt.setInt(1, productId);
                    transStmt.setInt(2, customerId);
                    transStmt.setInt(3, qty);

                    // Update stock
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE product SET quantity = quantity - ? WHERE id = ?");
                    updateStmt.setInt(1, qty);
                    updateStmt.setInt(2, productId);

                    conn.setAutoCommit(false);
                    transStmt.executeUpdate();
                    updateStmt.executeUpdate();
                    conn.commit();

                    JOptionPane.showMessageDialog(this, "✅ Transaction recorded successfully!");
                    qtyField.setText("");
                    loadAllData();
                    loadComboBoxData(productCombo, customerCombo);
                }
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        // Show the same transaction table from dashboard
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadComboBoxData(JComboBox<String> productCombo, JComboBox<String> customerCombo) {
        try {
            Connection conn = DBConnection.getConnection();
            
            // Load products with stock > 0
            productCombo.removeAllItems();
            PreparedStatement stmt = conn.prepareStatement("SELECT id, name, quantity FROM product WHERE quantity > 0");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                productCombo.addItem(rs.getInt("id") + " - " + rs.getString("name") + " (Stock: " + rs.getInt("quantity") + ")");
            }

            // Load customers
            customerCombo.removeAllItems();
            stmt = conn.prepareStatement("SELECT id, name FROM customer");
            rs = stmt.executeQuery();
            while (rs.next()) {
                customerCombo.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAllData() {
        loadProductData();
        loadCustomerData();
        loadSupplierData();
        loadTransactionData();
        updateStats();
    }

    private void loadProductData() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM product ORDER BY id");

            productTableModel.setRowCount(0);
            while (rs.next()) {
                int qty = rs.getInt("quantity");
                String status = qty == 0 ? "❌ Out of Stock" : qty < 10 ? "⚠️ Low Stock" : "✅ In Stock";
                
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    qty,
                    String.format("₹%.2f", rs.getDouble("price")),
                    status
                };
                productTableModel.addRow(row);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerData() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customer ORDER BY id");

            customerTableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                };
                customerTableModel.addRow(row);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSupplierData() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM supplier ORDER BY id");

            supplierTableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact")
                };
                supplierTableModel.addRow(row);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactionData() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT t.id, p.name as product_name, c.name as customer_name, t.quantity, t.date " +
                "FROM transaction t " +
                "JOIN product p ON t.product_id = p.id " +
                "JOIN customer c ON t.customer_id = c.id " +
                "ORDER BY t.date DESC LIMIT 10"
            );

            transactionTableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("product_name"),
                    rs.getString("customer_name"),
                    rs.getInt("quantity"),
                    rs.getTimestamp("date").toString().substring(0, 16)
                };
                transactionTableModel.addRow(row);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStats() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM product");
            if (rs.next()) statsProducts.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM customer");
            if (rs.next()) statsCustomers.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM supplier");
            if (rs.next()) statsSuppliers.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM transaction");
            if (rs.next()) statsTransactions.setText(String.valueOf(rs.getInt(1)));

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Using default look and feel");
        }

        SwingUtilities.invokeLater(() -> {
            new InventoryGUI().setVisible(true);
        });
    }
}