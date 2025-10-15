-- ===================================================================
-- COMPREHENSIVE INVENTORY MANAGEMENT DATABASE WITH DISCOUNT SUPPORT
-- ===================================================================

-- Create database
DROP DATABASE IF EXISTS inventorydb;
CREATE DATABASE inventorydb;
USE inventorydb;

-- Product table with profit tracking
CREATE TABLE product (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    cost_price DOUBLE NOT NULL DEFAULT 0.0 COMMENT 'Cost price for profit calculation',
    profit_margin DOUBLE GENERATED ALWAYS AS ((price - cost_price) / cost_price * 100) STORED COMMENT 'Profit margin percentage'
);

-- Customer table  
CREATE TABLE customer (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- Supplier table
CREATE TABLE supplier (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50)
);

-- Transaction table with profit tracking and discount functionality
CREATE TABLE transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    customer_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DOUBLE DEFAULT NULL COMMENT 'Cost price at time of sale for profit calculation',
    unit_price DOUBLE DEFAULT NULL COMMENT 'Selling price per unit',
    subtotal DOUBLE DEFAULT NULL COMMENT 'Subtotal (quantity × unit_price)',
    discount_percent DOUBLE DEFAULT 0 COMMENT 'Discount percentage applied',
    discount_amount DOUBLE DEFAULT NULL COMMENT 'Actual discount amount in currency',
    tax_amount DOUBLE DEFAULT NULL COMMENT 'GST/Tax amount (18% of discounted subtotal)',
    total_with_tax DOUBLE DEFAULT NULL COMMENT 'Final total amount including tax after discount',
    profit_amount DOUBLE DEFAULT NULL COMMENT 'Actual profit earned: (discounted_unit_price - unit_cost) × quantity',
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    INDEX idx_date (date),
    INDEX idx_product (product_id),
    INDEX idx_customer (customer_id)
);

-- ===================================================================
-- SAMPLE DATA INSERTION
-- ===================================================================

-- Insert sample products with cost prices for profit calculation
INSERT INTO product (id, name, quantity, price, cost_price) VALUES 
(1, 'Laptop', 15, 64999.0, 45000.0),      -- ₹19,999 profit per unit (44.4% margin)
(2, 'Mouse', 50, 500.0, 300.0),           -- ₹200 profit per unit (66.7% margin) 
(3, 'Keyboard', 25, 2500.0, 1800.0),      -- ₹700 profit per unit (38.9% margin)
(4, 'Monitor', 8, 15000.0, 12000.0),      -- ₹3,000 profit per unit (25.0% margin)
(5, 'Headphones', 30, 3500.0, 2200.0);   -- ₹1,300 profit per unit (59.1% margin)

-- Insert sample customers
INSERT INTO customer (id, name, email) VALUES 
(1, 'Shahinsha', 'shahinsha@gmail.com'),
(2, 'Finan', 'finan@gmail.com'),
(3, 'John Doe', 'john.doe@email.com'),
(4, 'Jane Smith', 'jane.smith@email.com');

-- Insert sample suppliers
INSERT INTO supplier (id, name, contact) VALUES 
(1, 'Tech Supplies Ltd.', '9876543210'),
(2, 'Global Electronics', '9123456789'),
(3, 'Computer World', '8765432109'),
(4, 'Digital Solutions', '7654321098');

-- ===================================================================
-- SAMPLE TRANSACTIONS WITH PROFIT TRACKING (NO DISCOUNT)
-- ===================================================================

-- Transaction 1: Laptop sale
-- Cost: ₹45,000, Selling Price: ₹64,999, Subtotal: ₹64,999, Tax: ₹11,699.82, Total: ₹76,698.82, Profit: ₹19,999
INSERT INTO transaction (product_id, customer_id, quantity, unit_cost, unit_price, subtotal, tax_amount, total_with_tax, profit_amount) 
VALUES (1, 1, 1, 45000.0, 64999.0, 64999.0, 11699.82, 76698.82, 19999.0);

-- Transaction 2: Mouse sale (quantity 2)
-- Cost: ₹600 (2×₹300), Selling Price: ₹1,000 (2×₹500), Subtotal: ₹1,000, Tax: ₹180, Total: ₹1,180, Profit: ₹400
INSERT INTO transaction (product_id, customer_id, quantity, unit_cost, unit_price, subtotal, tax_amount, total_with_tax, profit_amount) 
VALUES (2, 2, 2, 300.0, 500.0, 1000.0, 180.0, 1180.0, 400.0);

-- Transaction 3: Keyboard sale
-- Cost: ₹1,800, Selling Price: ₹2,500, Subtotal: ₹2,500, Tax: ₹450, Total: ₹2,950, Profit: ₹700
INSERT INTO transaction (product_id, customer_id, quantity, unit_cost, unit_price, subtotal, tax_amount, total_with_tax, profit_amount) 
VALUES (3, 3, 1, 1800.0, 2500.0, 2500.0, 450.0, 2950.0, 700.0);

-- Transaction 4: Monitor sale
-- Cost: ₹12,000, Selling Price: ₹15,000, Subtotal: ₹15,000, Tax: ₹2,700, Total: ₹17,700, Profit: ₹3,000
INSERT INTO transaction (product_id, customer_id, quantity, unit_cost, unit_price, subtotal, tax_amount, total_with_tax, profit_amount) 
VALUES (4, 4, 1, 12000.0, 15000.0, 15000.0, 2700.0, 17700.0, 3000.0);

-- Transaction 5: Headphones sale (quantity 2)
-- Cost: ₹4,400 (2×₹2,200), Selling Price: ₹7,000 (2×₹3,500), Subtotal: ₹7,000, Tax: ₹1,260, Total: ₹8,260, Profit: ₹2,600
INSERT INTO transaction (product_id, customer_id, quantity, unit_cost, unit_price, subtotal, tax_amount, total_with_tax, profit_amount) 
VALUES (5, 1, 2, 2200.0, 3500.0, 7000.0, 1260.0, 8260.0, 2600.0);

-- ===================================================================
-- DATABASE VERIFICATION AND OPTIMIZATION
-- ===================================================================

-- Update product quantities to reflect transactions
UPDATE product SET quantity = quantity - 1 WHERE id = 1; -- Laptop (1 sold)
UPDATE product SET quantity = quantity - 2 WHERE id = 2; -- Mouse (2 sold)  
UPDATE product SET quantity = quantity - 1 WHERE id = 3; -- Keyboard (1 sold)
UPDATE product SET quantity = quantity - 1 WHERE id = 4; -- Monitor (1 sold)
UPDATE product SET quantity = quantity - 2 WHERE id = 5; -- Headphones (2 sold)

-- ===================================================================
-- VERIFICATION QUERIES
-- ===================================================================

-- Show table structures
SELECT 'Product Table Structure:' as Info;
DESCRIBE product;

SELECT 'Customer Table Structure:' as Info;  
DESCRIBE customer;

SELECT 'Transaction Table Structure (with discount support):' as Info;
DESCRIBE transaction;

-- Show sample data
SELECT 'Sample Products:' as Info;
SELECT id, name, quantity, CONCAT('₹', FORMAT(price, 2)) as price FROM product;

SELECT 'Sample Customers:' as Info;
SELECT id, name, email FROM customer;

SELECT 'Sample Transactions with Discounts:' as Info;
SELECT 
    t.id as 'Sale ID',
    p.name as 'Product',
    c.name as 'Customer', 
    t.quantity as 'Qty',
    CONCAT('₹', FORMAT(p.price, 2)) as 'Unit Price',
    CONCAT('₹', FORMAT(t.quantity * p.price, 2)) as 'Subtotal',
    CONCAT(t.discount_percent, '%') as 'Discount %',
    CONCAT('₹', FORMAT(t.total_with_tax, 2)) as 'Total (Inc. Tax)',
    DATE_FORMAT(t.date, '%Y-%m-%d %H:%i') as 'Date'
FROM transaction t
JOIN product p ON t.product_id = p.id  
JOIN customer c ON t.customer_id = c.id
ORDER BY t.id;

-- ===================================================================
-- USEFUL TESTING AND DEBUGGING QUERIES
-- ===================================================================

-- Quick test to check transaction data format and display
-- (Run this to verify transaction history shows correct totals)
/*
SELECT 
    t.id as 'Sale ID',
    p.name as 'Product',
    c.name as 'Customer',
    t.quantity as 'Qty',
    COALESCE(t.unit_price, p.price) as 'Unit Price',
    COALESCE(t.subtotal, t.quantity * p.price) as 'Subtotal',
    COALESCE(t.tax_amount, t.quantity * p.price * 0.18) as 'GST (18%)',
    COALESCE(t.total_with_tax, (t.quantity * p.price * 1.18)) as 'Grand Total',
    COALESCE(t.profit_amount, 0) as 'Profit',
    DATE_FORMAT(t.date, '%Y-%m-%d %H:%i:%s') as 'Date Time'
FROM transaction t 
JOIN product p ON t.product_id = p.id 
JOIN customer c ON t.customer_id = c.id 
ORDER BY t.date DESC 
LIMIT 5;
*/

-- Delete all products and transactions (CAUTION: Use only when needed)
/*
DELETE FROM transaction;
DELETE FROM product;
ALTER TABLE transaction AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;
SELECT 'All products and transactions deleted successfully!' as Status;
*/

-- Success message
SELECT '✅ INVENTORY DATABASE WITH PROFIT TRACKING CREATED SUCCESSFULLY! ✅' as Status;
SELECT 'Ready to use with Java Inventory Management System' as Message;
SELECT 'Profit tracking and transaction history fully operational!' as Feature;
