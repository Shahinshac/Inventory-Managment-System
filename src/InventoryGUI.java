import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
public class InventoryGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel productTableModel, customerTableModel, supplierTableModel, transactionTableModel;
    private JTable productTable, customerTable, supplierTable, transactionTable;
    private JLabel statsProducts, statsCustomers, statsSuppliers, statsTransactions;
    private JLabel totalRevenue, totalProfit;
    private DecimalFormat currencyFormat;
    private JLabel statusLabel;
    private DefaultTableModel multiCartTableModel;
    private JLabel multiCartSubtotalLabel, multiCartGrandTotalLabel;
    private java.util.List<SimpleCartItem> shoppingCart = new java.util.ArrayList<>();
    private JComboBox<String> cartProductCombo;
    private JComboBox<String> stockProductCombo;
    public InventoryGUI() {
        currencyFormat = new DecimalFormat("₹#,##0.00");
        setTitle("₹ Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) {}
        testDatabaseConnection();
        initComponents();
        loadAllData();
    }
    private void testDatabaseConnection() {
        try {
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
                conn.close();
                System.out.println("✓ Database connected successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "❌ Database Error: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(0, 80));
        header.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("₹" + " Professional Inventory Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Real-time Financial Analytics", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitle.setForeground(new Color(220, 220, 220));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        
        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        tabbedPane.addTab("★" + " Financial Dashboard", createEnhancedDashboard());
        tabbedPane.addTab("▒" + " Products", createEnhancedProductPanel());
        tabbedPane.addTab("█" + " Stock Management", createStockManagementPanel());
        tabbedPane.addTab("☺" + " Customers", createCustomerPanel());
        tabbedPane.addTab("⚙" + " Suppliers", createSupplierPanel());
        tabbedPane.addTab("🛒 Shopping Cart", createShoppingCartPanel());
        tabbedPane.addTab("$" + " Transactions", createSimpleTransactionPanel());
        
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 5) refreshShoppingCartProducts();
            else if (selectedIndex == 2) refreshStockManagementProducts();
        });
        
        add(tabbedPane, BorderLayout.CENTER);
        createStatusBar();
    }
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel("✅ System Ready - Data loaded successfully");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel timeLabel = new JLabel();
        timeLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        javax.swing.Timer timeTimer = new javax.swing.Timer(1000, _ -> 
            timeLabel.setText("🕐 " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
        timeTimer.start();
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);
    }
    private JPanel createEnhancedDashboard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsProducts = new JLabel("0", SwingConstants.CENTER);
        statsCustomers = new JLabel("0", SwingConstants.CENTER);
        statsSuppliers = new JLabel("0", SwingConstants.CENTER);
        statsTransactions = new JLabel("0", SwingConstants.CENTER);
        totalRevenue = new JLabel("₹0.00", SwingConstants.CENTER);
        totalProfit = new JLabel("₹0.00", SwingConstants.CENTER);
        statsPanel.add(createEnhancedStatCard("▒ Total Products", statsProducts, new Color(52, 152, 219)));
        statsPanel.add(createEnhancedStatCard("☺ Total Customers", statsCustomers, new Color(46, 204, 113)));
        statsPanel.add(createEnhancedStatCard("⚙ Total Suppliers", statsSuppliers, new Color(231, 76, 60)));
        statsPanel.add(createEnhancedStatCard("$ Total Sales", statsTransactions, new Color(243, 156, 18)));
        statsPanel.add(createEnhancedStatCard("₹ Total Revenue", totalRevenue, new Color(155, 89, 182)));
        statsPanel.add(createEnhancedStatCard("↗ Total Profit", totalProfit, new Color(26, 188, 156)));
        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createEnhancedStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    private JPanel createEnhancedProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 245));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("+ Add New Product with Pricing"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JTextField qtyField = new JTextField(10);
        JTextField costField = new JTextField(15);
        JTextField priceField = new JTextField(15);
        JLabel profitLabel = new JLabel("Profit Margin: 0.0%");
        profitLabel.setFont(new Font("Arial", Font.BOLD, 12));
        profitLabel.setForeground(new Color(46, 204, 113));
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1; formPanel.add(idField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 3; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; formPanel.add(qtyField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Cost Price (₹):"), gbc);
        gbc.gridx = 3; formPanel.add(costField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Selling Price (₹):"), gbc);
        gbc.gridx = 1; formPanel.add(priceField, gbc);
        gbc.gridx = 2; formPanel.add(profitLabel, gbc);
        Runnable updateProfitCalculation = () -> {
            try {
                String costText = costField.getText().trim();
                String priceText = priceField.getText().trim();
                if (!costText.isEmpty() && !priceText.isEmpty()) {
                    double cost = Double.parseDouble(costText);
                    double price = Double.parseDouble(priceText);
                    if (cost > 0) {
                        double profitMargin = ((price - cost) / cost) * 100;
                        profitLabel.setText(String.format("Profit Margin: %.1f%%", profitMargin));
                        if (profitMargin < 0) profitLabel.setForeground(new Color(231, 76, 60));
                        else if (profitMargin < 10) profitLabel.setForeground(new Color(243, 156, 18));
                        else profitLabel.setForeground(new Color(46, 204, 113));
                    }
                } else {
                    profitLabel.setText("Profit Margin: 0.0%");
                    profitLabel.setForeground(new Color(46, 204, 113));
                }
            } catch (NumberFormatException e) {
                profitLabel.setText("Profit Margin: Invalid");
                profitLabel.setForeground(new Color(231, 76, 60));
            }
        };
        costField.addCaretListener(_ -> updateProfitCalculation.run());
        priceField.addCaretListener(_ -> updateProfitCalculation.run());
        JButton addBtn = createStyledButton("+ Add Product", new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(addBtn, gbc);

        addBtn.addActionListener(_ -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                int qty = Integer.parseInt(qtyField.getText().trim());
                double cost = Double.parseDouble(costField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "⚠️ Product name cannot be empty!"); return; }
                if (cost <= 0) { JOptionPane.showMessageDialog(this, "⚠️ Cost price must be greater than 0!"); return; }
                if (price <= cost) {
                    int confirm = JOptionPane.showConfirmDialog(this, "⚠️ Warning: Selling price is not higher than cost price.\nContinue anyway?", "Profit Warning", JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) return;
                }
                double profitMargin = ((price - cost) / cost) * 100;
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO product (id, name, quantity, price, cost_price) VALUES (?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE name=VALUES(name), quantity=VALUES(quantity), price=VALUES(price), cost_price=VALUES(cost_price)")) {
                    stmt.setInt(1, id); stmt.setString(2, name); stmt.setInt(3, qty); stmt.setDouble(4, price); stmt.setDouble(5, cost);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, String.format("✅ Product added/updated!\n\nID: %d | Name: %s\nStock: %d | Cost: %s | Price: %s\nProfit Margin: %.1f%%", 
                        id, name, qty, currencyFormat.format(cost), currencyFormat.format(price), profitMargin), "Success", JOptionPane.INFORMATION_MESSAGE);
                    idField.setText(""); nameField.setText(""); qtyField.setText(""); costField.setText(""); priceField.setText("");
                    profitLabel.setText("Profit Margin: 0.0%");
                    profitLabel.setForeground(new Color(46, 204, 113));
                    loadProductData();
                    updateEnhancedStats();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid input! Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        panel.add(formPanel, BorderLayout.NORTH);
        String[] columns = {"ID", "Product Name", "Stock Qty", "Cost Price", "Selling Price", "Profit Margin", "Status"};
        productTableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(35);
        productTable.setFont(new Font("Arial", Font.PLAIN, 12));
        productTable.getTableHeader().setBackground(new Color(52, 73, 94));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📦 Product Inventory with Financial Details"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel for delete functionality
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton deleteBtn = createStyledButton("🗑️ Delete Selected Product", new Color(231, 76, 60));
        deleteBtn.addActionListener(_ -> deleteSelectedProduct());
        
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSimpleTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        
        String[] columns = {"Sale ID", "Product", "Customer", "Qty", "Cost Price", "Unit Price", "Total", "Profit", "Date & Time"};
        transactionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setRowHeight(35);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        transactionTable.getTableHeader().setBackground(new Color(52, 73, 94));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        transactionTable.setSelectionBackground(new Color(230, 230, 250));
        transactionTable.setGridColor(new Color(200, 200, 200));
        
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        transactionTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        transactionTable.getColumnModel().getColumn(7).setPreferredWidth(90);
        transactionTable.getColumnModel().getColumn(8).setPreferredWidth(120);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
        currencyRenderer.setHorizontalAlignment(JLabel.RIGHT);
        currencyRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        
        DefaultTableCellRenderer profitRenderer = new DefaultTableCellRenderer();
        profitRenderer.setHorizontalAlignment(JLabel.RIGHT);
        profitRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        profitRenderer.setForeground(new Color(46, 204, 113));
        
        transactionTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        transactionTable.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        transactionTable.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
        transactionTable.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
        transactionTable.getColumnModel().getColumn(7).setCellRenderer(profitRenderer);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("💳 Complete Transaction History"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton refreshBtn =         new JButton("🔄 Refresh");
        refreshBtn.addActionListener(_ -> loadTransactionData());
        
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private class SimpleCartItem {
        int productId;
        String productName;
        double unitPrice;
        int quantity;
        
        SimpleCartItem(int productId, String productName, double unitPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }
        double getSubtotal() {
            return unitPrice * quantity;
        }
    }
    private JPanel createShoppingCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        addItemPanel.setBorder(BorderFactory.createTitledBorder("➕ Add Item to Cart"));
        addItemPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        cartProductCombo = new JComboBox<>(); // Store reference for auto-refresh
        JTextField qtyField = new JTextField("1", 8);
        qtyField.setHorizontalAlignment(JTextField.CENTER);
        
        loadProductComboForCart(cartProductCombo);
        
        gbc.gridx = 0; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Select Product:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; cartProductCombo.setPreferredSize(new Dimension(350, 30));
        addItemPanel.add(cartProductCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        addItemPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        addItemPanel.add(qtyField, gbc);
        
        JButton addToCartBtn = new JButton("➕ Add to Cart");
        gbc.gridx = 2;
        addItemPanel.add(addToCartBtn, gbc);
        
        panel.add(addItemPanel, BorderLayout.NORTH);
        
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder("🛒 Shopping Cart"));
        cartPanel.setBackground(Color.WHITE);
        
        String[] columns = {"#", "Product", "Unit Price", "Quantity", "Subtotal", "Action"};
        multiCartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only to avoid freeze issues
            }
        };
        
        JTable cartTable = new JTable(multiCartTableModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 13));
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        cartTable.getTableHeader().setBackground(new Color(52, 152, 219));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        cartTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setPreferredSize(new Dimension(700, 300));
        cartPanel.add(cartScroll, BorderLayout.CENTER);
        
        cartTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = cartTable.rowAtPoint(evt.getPoint());
                int col = cartTable.columnAtPoint(evt.getPoint());
                if (col == 5 && row >= 0 && row < shoppingCart.size()) {
                    removeItemFromCart(row);
                }
            }
        });
        
        panel.add(cartPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        
        JPanel totalsPanel = new JPanel(new GridLayout(2, 1, 5, 10));
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Order Summary"),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        totalsPanel.setBackground(Color.WHITE);
        
        multiCartSubtotalLabel = new JLabel("Total Items: 0");
        multiCartSubtotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        multiCartSubtotalLabel.setForeground(new Color(52, 73, 94));
        
        multiCartGrandTotalLabel = new JLabel("TOTAL: ₹0.00");
        multiCartGrandTotalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        multiCartGrandTotalLabel.setForeground(new Color(46, 204, 113));
        
        totalsPanel.add(multiCartSubtotalLabel);
        totalsPanel.add(multiCartGrandTotalLabel);
        
        bottomPanel.add(totalsPanel, BorderLayout.EAST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBackground(new Color(245, 245, 245));
        
        JButton clearCartBtn =         new JButton("🗑️ Clear Cart");
        JButton checkoutBtn =         new JButton("💳 Checkout");
        
        clearCartBtn.setPreferredSize(new Dimension(150, 40));
        checkoutBtn.setPreferredSize(new Dimension(150, 40));
        
        buttonsPanel.add(clearCartBtn);
        buttonsPanel.add(checkoutBtn);
        
        bottomPanel.add(buttonsPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        addToCartBtn.addActionListener(_ -> addItemToShoppingCart(cartProductCombo, qtyField));
        clearCartBtn.addActionListener(_ -> clearShoppingCart());
        checkoutBtn.addActionListener(_ -> processMultiItemCheckout());
        
        return panel;
    }
    
    private void refreshShoppingCartProducts() {
        if (cartProductCombo != null) {
            String currentSelection = (String) cartProductCombo.getSelectedItem();
            loadProductComboForCart(cartProductCombo);
            
            if (currentSelection != null && !currentSelection.equals("Select Product")) {
                for (int i = 0; i < cartProductCombo.getItemCount(); i++) {
                    if (cartProductCombo.getItemAt(i).equals(currentSelection)) {
                        cartProductCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }
    
    private void refreshStockManagementProducts() {
        if (stockProductCombo != null) {
            String currentSelection = (String) stockProductCombo.getSelectedItem();
            loadProductComboForStock(stockProductCombo);
            
            if (currentSelection != null && !currentSelection.equals("Select Product")) {
                for (int i = 0; i < stockProductCombo.getItemCount(); i++) {
                    if (stockProductCombo.getItemAt(i).equals(currentSelection)) {
                        stockProductCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }
    
    private void addItemToShoppingCart(JComboBox<String> productCombo, JTextField qtyField) {
        try {
            String selected = (String) productCombo.getSelectedItem();
            if (selected == null || selected.equals("Select Product")) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a product!");
                return;
            }
            
            String qtyText = qtyField.getText().trim();
            if (qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please enter a quantity!");
                return;
            }
            
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Quantity must be greater than 0!");
                return;
            }
            
            int productId = Integer.parseInt(selected.split(" - ")[0].trim());
            String productName = selected.split(" - ")[1].split(" \\(")[0].trim();
            
            double unitPrice = 0.0;
            int availableStock = 0;
            
            if (selected.contains("₹")) {
                String priceStr = selected.split("₹")[1].split(",")[0].trim();
                unitPrice = Double.parseDouble(priceStr);
            }
            
            if (selected.contains("Stock: ")) {
                String stockStr = selected.split("Stock: ")[1];
                stockStr = stockStr.split("[^0-9]")[0].trim();
                availableStock = Integer.parseInt(stockStr);
            }
            
            int currentCartQty = 0;
            for (SimpleCartItem item : shoppingCart) {
                if (item.productId == productId) {
                    currentCartQty = item.quantity;
                    break;
                }
            }
            
            int totalRequested = currentCartQty + qty;
            if (totalRequested > availableStock) {
                JOptionPane.showMessageDialog(this, 
                    String.format("❌ Insufficient stock!\n\nProduct: %s\nAvailable: %d | In Cart: %d | Requested: %d | Total: %d\n\nReduce quantity or remove from cart.", 
                        productName, availableStock, currentCartQty, qty, totalRequested),
                    "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean found = false;
            for (SimpleCartItem item : shoppingCart) {
                if (item.productId == productId) {
                    item.quantity += qty;
                    found = true;
                    break;
                }
            }
            if (!found) {
                shoppingCart.add(new SimpleCartItem(productId, productName, unitPrice, qty));
            }
            updateMultiCartDisplay();
            qtyField.setText("1");
            
            JOptionPane.showMessageDialog(this, 
                String.format("✅ Added to cart!\n\nProduct: %s | Qty: %d | Price: %s\nRemaining Stock: %d units", 
                    productName, qty, currencyFormat.format(unitPrice), availableStock - totalRequested),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Error: Please enter a valid quantity!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void removeItemFromCart(int index) {
        if (index >= 0 && index < shoppingCart.size()) {
            SimpleCartItem item = shoppingCart.get(index);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + item.productName + " from cart?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                shoppingCart.remove(index);
                updateMultiCartDisplay();
            }
        }
    }
    
    private void clearShoppingCart() {
        if (shoppingCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is already empty!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Clear all items from cart?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            shoppingCart.clear();
            updateMultiCartDisplay();
            JOptionPane.showMessageDialog(this, "Cart cleared!");
        }
    }
    
    private void updateMultiCartDisplay() {
        multiCartTableModel.setRowCount(0);
        
        double total = 0.0;
        int totalQty = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            SimpleCartItem item = shoppingCart.get(i);
            double itemSubtotal = item.getSubtotal();
            total += itemSubtotal;
            totalQty += item.quantity;
            
            Object[] row = {
                (i + 1),
                item.productName,
                currencyFormat.format(item.unitPrice),
                item.quantity,
                currencyFormat.format(itemSubtotal),
                "✕ Remove"
            };
            multiCartTableModel.addRow(row);
        }
        multiCartSubtotalLabel.setText("Total Items: " + shoppingCart.size() + " (" + totalQty + " units)");
        multiCartGrandTotalLabel.setText("TOTAL: " + currencyFormat.format(total));
    }
    
    private void processMultiItemCheckout() {
        if (shoppingCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Add items first.");
            return;
        }
        // Select customer
        String[] customers = getCustomerList();
        if (customers.length == 0) {
            JOptionPane.showMessageDialog(this, "No customers found! Add a customer first.");
            return;
        }
        String customer = (String) JOptionPane.showInputDialog(this, "Select Customer:", "Checkout", JOptionPane.QUESTION_MESSAGE, null, customers, customers[0]);
        
        if (customer == null) return;
        
        int customerId = Integer.parseInt(customer.split(" - ")[0]);
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            for (SimpleCartItem item : shoppingCart) {
                double costPrice = 0.0;
                try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COALESCE(cost_price, 0) as cost_price FROM product WHERE id = ?")) {
                    stmt.setInt(1, item.productId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        costPrice = rs.getDouble("cost_price");
                    }
                }
                double total = item.getSubtotal();
                double profit = (item.unitPrice - costPrice) * item.quantity;
                
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO transaction (product_id, customer_id, quantity, unit_price, " +
                    "subtotal, tax_amount, total_with_tax, profit_amount, date) " +
                    "VALUES (?, ?, ?, ?, ?, 0, ?, ?, NOW())")) {
                    stmt.setInt(1, item.productId);
                    stmt.setInt(2, customerId);
                    stmt.setInt(3, item.quantity);
                    stmt.setDouble(4, item.unitPrice);
                    stmt.setDouble(5, total);
                    stmt.setDouble(6, total);
                    stmt.setDouble(7, profit);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE product SET quantity = quantity - ? WHERE id = ?")) {
                    stmt.setInt(1, item.quantity);
                    stmt.setInt(2, item.productId);
                    stmt.executeUpdate();
                }
            }
            conn.commit();
            
            double total = shoppingCart.stream().mapToDouble(SimpleCartItem::getSubtotal).sum();
            int totalQty = shoppingCart.stream().mapToInt(item -> item.quantity).sum();
            
            String receipt = String.format("✅ Checkout Successful!\n\nCustomer: %s\nItems: %d (%d units) | Total: %s\n\nThank you!", 
                customer, shoppingCart.size(), totalQty, currencyFormat.format(total));
            
            JOptionPane.showMessageDialog(this, receipt, "Receipt", JOptionPane.INFORMATION_MESSAGE);
            
            shoppingCart.clear();
            updateMultiCartDisplay();
            loadAllData();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during checkout: " + e.getMessage());
        }
    }
    
    private String[] getCustomerList() {
        java.util.List<String> customers = new java.util.ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM customer ORDER BY name")) {
            while (rs.next()) {
                customers.add(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers.toArray(new String[0]);
    }
    private JPanel createStockManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("█" + " Stock Quantity Management"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        stockProductCombo = new JComboBox<>(); // Store reference for auto-refresh
        JTextField addQuantityField = new JTextField(10);
        JTextField removeQuantityField = new JTextField(10);
        JLabel currentStockLabel = new JLabel("Current Stock: 0");
        JLabel newStockLabel = new JLabel("New Stock: 0");
        
        currentStockLabel.setFont(new Font("Arial", Font.BOLD, 14)); currentStockLabel.setForeground(new Color(52, 73, 94));
        newStockLabel.setFont(new Font("Arial", Font.BOLD, 14)); newStockLabel.setForeground(new Color(46, 204, 113));

        loadProductComboForStock(stockProductCombo);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Select Product:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; formPanel.add(stockProductCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; formPanel.add(new JLabel("Add Quantity:"), gbc);
        gbc.gridx = 1; formPanel.add(addQuantityField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Remove Quantity:"), gbc);
        gbc.gridx = 3; formPanel.add(removeQuantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(currentStockLabel, gbc);
        gbc.gridx = 1; formPanel.add(newStockLabel, gbc);
        
        Runnable updateStockCalculation = () -> {
            try {
                String productItem = (String) stockProductCombo.getSelectedItem();
                if (productItem != null && !productItem.equals("Select Product")) {
                    if (productItem.contains("Stock: ")) {
                        String stockStr = productItem.split("Stock: ")[1].split("\\)")[0];
                        int currentStock = Integer.parseInt(stockStr);
                        currentStockLabel.setText("Current Stock: " + currentStock);
                        
                        int addQty = 0;
                        int removeQty = 0;
                        
                        String addText = addQuantityField.getText().trim();
                        String removeText = removeQuantityField.getText().trim();
                        
                        if (!addText.isEmpty()) addQty = Integer.parseInt(addText);
                        if (!removeText.isEmpty()) removeQty = Integer.parseInt(removeText);
                        
                        int newStock = currentStock + addQty - removeQty;
                        newStockLabel.setText("New Stock: " + newStock);
                        
                        if (newStock < 0) newStockLabel.setForeground(new Color(231, 76, 60));
                        else if (newStock < 10) newStockLabel.setForeground(new Color(243, 156, 18));
                        else newStockLabel.setForeground(new Color(46, 204, 113));
                    }
                }
            } catch (Exception e) {
                currentStockLabel.setText("Current Stock: 0");
                newStockLabel.setText("New Stock: 0");
            }
        };

        stockProductCombo.addActionListener(_ -> updateStockCalculation.run());
        addQuantityField.addCaretListener(_ -> updateStockCalculation.run());
        removeQuantityField.addCaretListener(_ -> updateStockCalculation.run());

        JButton updateBtn = createStyledButton("█" + " Update Stock", new Color(46, 204, 113));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(updateBtn, gbc);

        updateBtn.addActionListener(_ -> updateStock(stockProductCombo, addQuantityField, removeQuantityField, updateStockCalculation));

        panel.add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("📊 Current Stock Levels"));
        
        String[] stockColumns = {"ID", "Product Name", "Current Stock", "Unit Price", "Status"};
        DefaultTableModel stockTableModel = new DefaultTableModel(stockColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable stockTable = new JTable(stockTableModel);
        stockTable.setRowHeight(30);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        loadStockData(stockTableModel);
        
        JScrollPane stockScrollPane = new JScrollPane(stockTable);
        tablePanel.add(stockScrollPane, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Customer"));
        
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(20);
        JButton addBtn =         new JButton("Add Customer");

        formPanel.add(new JLabel("ID:")); formPanel.add(idField);
        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Email:")); formPanel.add(emailField);
        formPanel.add(addBtn);

        addBtn.addActionListener(_ -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name and email cannot be empty!");
                    return;
                }
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO customer (id, name, email) VALUES (?, ?, ?)")) {
                    
                    stmt.setInt(1, id);
                    stmt.setString(2, name);
                    stmt.setString(3, email);

                    int result = stmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "✅ Customer added successfully!");
                        idField.setText(""); nameField.setText(""); emailField.setText("");
                        loadCustomerData();
                        updateEnhancedStats();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

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

        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Supplier"));
        
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(15);
        JTextField contactField = new JTextField(20);
        JButton addBtn =         new JButton("Add Supplier");

        formPanel.add(new JLabel("ID:")); formPanel.add(idField);
        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Contact:")); formPanel.add(contactField);
        formPanel.add(addBtn);

        addBtn.addActionListener(_ -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();

                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name and contact cannot be empty!");
                    return;
                }
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO supplier (id, name, contact) VALUES (?, ?, ?)")) {
                    
                    stmt.setInt(1, id);
                    stmt.setString(2, name);
                    stmt.setString(3, contact);

                    int result = stmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "✅ Supplier added successfully!");
                        idField.setText(""); nameField.setText(""); contactField.setText("");
                        loadSupplierData();
                        updateEnhancedStats();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Contact"};
        supplierTableModel = new DefaultTableModel(columns, 0);
        supplierTable = new JTable(supplierTableModel);
        supplierTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Supplier List"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    private void loadTransactionData() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String query;
            boolean hasEnhancedColumns = false;
            
            try (ResultSet checkColumns = stmt.executeQuery(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = 'transaction' AND COLUMN_NAME IN ('unit_price', 'profit_amount', 'tax_amount')")) {
                
                if (checkColumns.next()) {
                    hasEnhancedColumns = true;
                }
                checkColumns.close();
            } catch (SQLException e) {
                hasEnhancedColumns = false;
            }
            if (hasEnhancedColumns) {
                query = "SELECT t.id, p.name as product_name, c.name as customer_name, " +
                       "t.quantity, " +
                       "COALESCE(p.cost_price, 0) as cost_price, " +
                       "COALESCE(t.unit_price, p.price) as unit_price, " +
                       "COALESCE(t.subtotal, t.quantity * COALESCE(t.unit_price, p.price)) as subtotal, " +
                       "COALESCE(t.discount_percent, 0) as discount_percent, " +
                       "COALESCE(t.discount_amount, 0) as discount_amount, " +
                       "COALESCE(t.tax_amount, 0) as tax_amount, " +
                       "COALESCE(t.total_with_tax, 0) as total_with_tax, " +
                       "COALESCE(t.profit_amount, 0) as profit_amount, " +
                       "DATE_FORMAT(t.date, '%d-%m-%Y %H:%i') as formatted_date " +
                       "FROM transaction t " +
                       "JOIN product p ON t.product_id = p.id " +
                       "JOIN customer c ON t.customer_id = c.id " +
                       "ORDER BY t.date DESC LIMIT 50";
            } else {
                query = "SELECT t.id, p.name as product_name, c.name as customer_name, " +
                       "t.quantity, COALESCE(p.cost_price, 0) as cost_price, p.price as unit_price, " +
                       "(t.quantity * p.price) as subtotal, " +
                       "(t.quantity * p.price * 0.18) as tax_amount, " +
                       "(t.quantity * p.price * 1.18) as total_with_tax, " +
                       "0 as profit_amount, " +
                       "DATE_FORMAT(t.date, '%Y-%m-%d %H:%i:%s') as formatted_date " +
                       "FROM transaction t " +
                       "JOIN product p ON t.product_id = p.id " +
                       "JOIN customer c ON t.customer_id = c.id " +
                       "ORDER BY t.date DESC LIMIT 20";
            }
            try (ResultSet rs = stmt.executeQuery(query)) {
                transactionTableModel.setRowCount(0);
                
                if (!rs.next()) {
                    transactionTableModel.addRow(new Object[]{
                        "No sales yet", "Add products and customers", "to start", "selling", "", "", "", "", ""
                    });
                } else {
                    do {
                        int quantity = rs.getInt("quantity");
                        double costPrice = rs.getDouble("cost_price");
                        double unitPrice = rs.getDouble("unit_price");
                        double grandTotal = rs.getDouble("total_with_tax");
                        double profit = rs.getDouble("profit_amount");
                        
                        String formattedCostPrice = currencyFormat.format(costPrice);
                        String formattedUnitPrice = currencyFormat.format(unitPrice);
                        String formattedGrandTotal = currencyFormat.format(grandTotal);
                        String formattedProfit = currencyFormat.format(profit);
                        
                        Object[] row = {
                            "#" + rs.getInt("id"),
                            rs.getString("product_name"),
                            rs.getString("customer_name"),
                            quantity,
                            formattedCostPrice,
                            formattedUnitPrice,
                            formattedGrandTotal,
                            formattedProfit,
                            rs.getString("formatted_date")
                        };
                        transactionTableModel.addRow(row);
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            transactionTableModel.setRowCount(0);
            String errorMsg = e.getMessage();
            if (errorMsg.contains("doesn't exist")) {
                transactionTableModel.addRow(new Object[]{
                    "⚠️ Table Missing", "Please import inventory.sql", "", "", "", "", "", "", ""
                });
            } else if (errorMsg.contains("Unknown column")) {
                transactionTableModel.addRow(new Object[]{
                    "⚠️ Column Missing", "Database schema needs updating", "", "", "", "", "", "", ""
                });
            } else {
                transactionTableModel.addRow(new Object[]{
                    "⚠️ Database Error", "Connection failed", "", "", "", "", "", "", ""
                });
            }
            System.err.println("Transaction data loading error: " + errorMsg);
        }
    }
    private void updateEnhancedStats() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM product");
            if (rs.next()) statsProducts.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM customer");
            if (rs.next()) statsCustomers.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM supplier");
            if (rs.next()) statsSuppliers.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM `transaction`");
            if (rs.next()) statsTransactions.setText(String.valueOf(rs.getInt(1)));

            // Financial metrics - calculate basic revenue (will be enhanced when total_with_tax is available)
            try {
                rs = stmt.executeQuery(
                    "SELECT COALESCE(SUM(t.quantity * p.price * 1.18), 0) " +
                    "FROM transaction t JOIN product p ON t.product_id = p.id");
                if (rs.next()) {
                    double totalRevenueWithTax = rs.getDouble(1);
                    totalRevenue.setText(currencyFormat.format(totalRevenueWithTax));
                    
                    // Estimated profit (20% margin on tax-inclusive revenue)
                    double estimatedProfit = totalRevenueWithTax * 0.2;
                    totalProfit.setText(currencyFormat.format(estimatedProfit));
                }
            } catch (SQLException statEx) {
                // Fallback if joins fail - show basic counts
                totalRevenue.setText("₹0.00");
                totalProfit.setText("₹0.00");
            }
        } catch (SQLException e) {
            System.err.println("Error updating enhanced statistics: " + e.getMessage());
        }
    }
    private void loadProductComboForCart(JComboBox<String> productCombo) {
        try (Connection conn = DBConnection.getConnection()) {
            productCombo.removeAllItems();
            productCombo.addItem("Select Product");
            
            String query;
            boolean useEnhanced = true;
            
            try (Statement testStmt = conn.createStatement()) {
                ResultSet testRs = testStmt.executeQuery("SELECT cost_price FROM product LIMIT 1");
                testRs.close();
                query = "SELECT id, name, price, quantity, COALESCE(cost_price, 0) as cost_price, " +
                       "COALESCE(profit_margin, 0) as profit_margin FROM product WHERE quantity > 0 ORDER BY name";
            } catch (SQLException e) {
                useEnhanced = false;
                query = "SELECT id, name, price, quantity FROM product WHERE quantity > 0 ORDER BY name";
            }
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String profitInfo = "";
                    if (useEnhanced) {
                        double profitMargin = rs.getDouble("profit_margin");
                        profitInfo = profitMargin > 0 ? 
                            String.format(" | Profit: %.1f%%", profitMargin) : "";
                    }
                    productCombo.addItem(String.format("%d - %s (₹%.2f, Stock: %d%s)", 
                        rs.getInt("id"), rs.getString("name"), 
                        rs.getDouble("price"), rs.getInt("quantity"), profitInfo));
                }
                if (productCombo.getItemCount() == 1) { // Only "Select Product" item
                    productCombo.addItem("No products available");
                }
            }
        } catch (SQLException e) {
            productCombo.removeAllItems();
            productCombo.addItem("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadProductData() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String query;
            boolean useEnhanced = true;
            
            try {
                ResultSet testRs = stmt.executeQuery("SELECT cost_price FROM product LIMIT 1");
                testRs.close();
                query = "SELECT id, name, quantity, price, " +
                       "COALESCE(cost_price, 0) as cost_price, " +
                       "COALESCE(profit_margin, 0) as profit_margin " +
                       "FROM product ORDER BY id";
            } catch (SQLException e) {
                // Fallback to basic query
                useEnhanced = false;
                query = "SELECT id, name, quantity, price FROM product ORDER BY id";
            }
            ResultSet rs = stmt.executeQuery(query);
            productTableModel.setRowCount(0);
            
            while (rs.next()) {
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                
                String status = qty == 0 ? "❌ Out of Stock" : 
                               qty < 10 ? "⚠️ Low Stock" : "✅ In Stock";
                
                if (useEnhanced) {
                    double costPrice = rs.getDouble("cost_price");
                    double profitMargin = rs.getDouble("profit_margin");
                    
                    // Display profit margin percentage with color coding
                    String profitDisplay;
                    if (costPrice > 0) {
                        profitDisplay = String.format("%.1f%%", profitMargin);
                    } else {
                        profitDisplay = "N/A";
                    }
                    
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        qty,
                        costPrice > 0 ? currencyFormat.format(costPrice) : "N/A",
                        currencyFormat.format(price), // Selling Price
                        profitDisplay,
                        status
                    };
                    productTableModel.addRow(row);
                } else {
                    // Basic display without cost/profit info
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        qty,
                        "N/A", // Cost price
                        currencyFormat.format(price), // Selling Price
                        "N/A", // Profit amount  
                        status
                    };
                    productTableModel.addRow(row);
                }
            }
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Please select a product to delete!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int productId = (int) productTableModel.getValueAt(selectedRow, 0);
            String productName = (String) productTableModel.getValueAt(selectedRow, 1);
            
            // Check if product has any transactions
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT COUNT(*) as count FROM transaction WHERE product_id = ?")) {
                
                checkStmt.setInt(1, productId);
                ResultSet rs = checkStmt.executeQuery();
                
                int transactionCount = 0;
                if (rs.next()) {
                    transactionCount = rs.getInt("count");
                }
                
                String confirmMessage;
                if (transactionCount > 0) {
                    confirmMessage = String.format(
                        "⚠️ Warning: This product has %d transaction(s) in history.\n\n" +
                        "Product ID: %d\nProduct Name: %s\n\n" +
                        "Are you sure you want to delete this product?\n" +
                        "(Transaction history will remain but will show as 'Deleted Product')",
                        transactionCount, productId, productName);
                } else {
                    confirmMessage = String.format(
                        "Are you sure you want to delete this product?\n\n" +
                        "Product ID: %d\nProduct Name: %s",
                        productId, productName);
                }
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    confirmMessage,
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Delete the product
                    try (PreparedStatement deleteStmt = conn.prepareStatement(
                        "DELETE FROM product WHERE id = ?")) {
                        
                        deleteStmt.setInt(1, productId);
                        int rowsAffected = deleteStmt.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this,
                                String.format("✅ Product deleted successfully!\n\nProduct ID: %d | Name: %s",
                                    productId, productName),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Refresh all data
                            loadProductData();
                            updateEnhancedStats();
                            
                            // Refresh combo boxes if on shopping cart or stock management tabs
                            if (cartProductCombo != null) {
                                loadProductComboForCart(cartProductCombo);
                            }
                            if (stockProductCombo != null) {
                                loadProductComboForStock(stockProductCombo);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "❌ Failed to delete product. Please try again.",
                                "Delete Failed",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "❌ Database Error: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "❌ Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void loadCustomerData() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customer ORDER BY id")) {

            customerTableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                };
                customerTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    private void loadSupplierData() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM supplier ORDER BY id")) {

            supplierTableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact")
                };
                supplierTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    private void loadAllData() {
        loadProductData();
        loadCustomerData();
        loadSupplierData();
        loadTransactionData();
        updateEnhancedStats();
    }
    // Windows-compatible icons using Unicode and symbols
    private void loadProductComboForStock(JComboBox<String> productCombo) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name, price, quantity FROM product");
             ResultSet rs = stmt.executeQuery()) {
            
            productCombo.removeAllItems();
            productCombo.addItem("Select Product");
            
            while (rs.next()) {
                productCombo.addItem(String.format("%d - %s (Price: ₹%.2f, Stock: %d)", 
                    rs.getInt("id"), rs.getString("name"), 
                    rs.getDouble("price"), rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateStock(JComboBox<String> productCombo, JTextField addField, JTextField removeField, Runnable updateCalc) {
        try {
            String productItem = (String) productCombo.getSelectedItem();
            if (productItem == null || productItem.equals("Select Product")) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a product!");
                return;
            }
            int productId = Integer.parseInt(productItem.split(" - ")[0]);
            
            String addText = addField.getText().trim();
            String removeText = removeField.getText().trim();
            
            int addQty = addText.isEmpty() ? 0 : Integer.parseInt(addText);
            int removeQty = removeText.isEmpty() ? 0 : Integer.parseInt(removeText);
            
            if (addQty < 0 || removeQty < 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Quantities must be positive numbers!");
                return;
            }
            if (addQty == 0 && removeQty == 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Please enter quantity to add or remove!");
                return;
            }
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try {
                // Get current stock
                int currentStock = 0;
                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT quantity, name FROM product WHERE id = ?")) {
                    checkStmt.setInt(1, productId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        currentStock = rs.getInt("quantity");
                        String productName = rs.getString("name");
                        
                        int newStock = currentStock + addQty - removeQty;
                        if (newStock < 0) {
                            JOptionPane.showMessageDialog(this, 
                                String.format("❌ Insufficient stock!\n\nProduct: %s\nCurrent Stock: %d\nTrying to Remove: %d", 
                                productName, currentStock, removeQty));
                            return;
                        }
                        // Update stock
                        try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE product SET quantity = ? WHERE id = ?")) {
                            updateStmt.setInt(1, newStock);
                            updateStmt.setInt(2, productId);
                            updateStmt.executeUpdate();
                        }
                        conn.commit();
                        
                        String operation = "";
                        if (addQty > 0) operation += "Added " + addQty + " units";
                        if (removeQty > 0) {
                            if (!operation.isEmpty()) operation += ", ";
                            operation += "Removed " + removeQty + " units";
                        }
                        JOptionPane.showMessageDialog(this, 
                            String.format("✅ Stock updated successfully!\n\n" +
                            "Product: %s\n%s\nNew Stock Level: %d units", 
                            productName, operation, newStock));
                        
                        // Clear fields and refresh
                        addField.setText("");
                        removeField.setText("");
                        loadProductComboForStock(productCombo);
                        updateCalc.run();
                        loadAllData(); // Refresh all data
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error updating stock: " + e.getMessage());
        }
    }
    private void loadStockData(DefaultTableModel stockTableModel) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, quantity, price FROM product ORDER BY name")) {

            stockTableModel.setRowCount(0);
            
            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                
                String status;
                if (quantity == 0) {
                    status = "🔴 Out of Stock";
                } else if (quantity <= 5) {
                    status = "🟡 Low Stock";
                } else if (quantity <= 20) {
                    status = "🟢 Normal";
                } else {
                    status = "🔵 High Stock";
                }
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    quantity + " units",
                    currencyFormat.format(price),
                    status
                };
                stockTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
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
