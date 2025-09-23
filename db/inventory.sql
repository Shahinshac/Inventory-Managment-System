-- Create database
CREATE DATABASE IF NOT EXISTS inventorydb;
USE inventorydb;

-- Product table
CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL
);

-- Customer table
CREATE TABLE IF NOT EXISTS customer (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- Supplier table
CREATE TABLE IF NOT EXISTS supplier (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50)
);

-- Transaction table
CREATE TABLE IF NOT EXISTS transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    customer_id INT,
    quantity INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Insert some sample data
INSERT INTO product VALUES (1, 'Laptop', 10, 55000.0);
INSERT INTO product VALUES (2, 'Mouse', 50, 500.0);

INSERT INTO customer VALUES (1, 'Rahul', 'rahul@example.com');
INSERT INTO customer VALUES (2, 'Anjali', 'anjali@example.com');

INSERT INTO supplier VALUES (1, 'Tech Supplies Ltd.', '9876543210');
INSERT INTO supplier VALUES (2, 'Global Electronics', '9123456789');
