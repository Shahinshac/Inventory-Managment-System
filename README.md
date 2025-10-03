# 📦 Inventory Management System - Desktop GUI

A clean, simple desktop GUI application for managing inventory with MySQL database.

## ✨ Features

- **📊 Dashboard**: Statistics overview and recent transactions
- **📦 Products**: Add/view products with stock status indicators
- **👥 Customers**: Customer management
- **🚛 Suppliers**: Supplier information tracking
- **💳 Transactions**: Sales recording with automatic stock updates

## 📁 Clean File Structure

```
📦 Inventory-Management-System/
├── 🎯 InventoryGUI.java         # Main GUI application (single file)
├── 🔗 DBConnection.java         # Database connectivity
├── 📄 Product.java              # Product model class
├── 📄 Customer.java             # Customer model class  
├── 📄 Supplier.java             # Supplier model class
├── 📁 db/
│   └── inventory.sql            # Database schema with sample data
├── 📁 lib/                      # Place MySQL JDBC driver here
├── 📁 build/                    # Compiled classes (auto-generated)
├── 🚀 setup.bat                 # Easy setup script
├── 🚀 run.bat                   # Easy run script
└── 📖 README.md                 # This file
```

## 🚀 Quick Setup & Run

### 1. Prerequisites
- ☕ Java JDK 8 or higher
- 🐬 MySQL (via XAMPP recommended)
- 🔌 MySQL JDBC Driver

### 2. Database Setup
1. **Start XAMPP** → Start MySQL service
2. **Open phpMyAdmin** → Go to http://localhost/phpmyadmin
3. **Create Database** → Create `inventorydb`  
4. **Import Schema** → Import `db/inventory.sql`

### 3. Get MySQL JDBC Driver
1. Download from: https://dev.mysql.com/downloads/connector/j/
2. Get `mysql-connector-java-8.0.33.jar`
3. Place in `lib/` folder

### 4. Setup & Run
```batch
# Setup (compile everything)
setup.bat

# Run the application  
run.bat
```

## 🎯 Application Features

### Dashboard Tab 📊
- **Color-coded Statistics Cards**: Products, Customers, Suppliers, Transactions
- **Recent Transactions Table**: Latest 10 transactions with details

### Products Tab 📦  
- **Add Products**: ID, Name, Quantity, Price
- **Product List**: All products with stock status
- **Stock Indicators**: 
  - ✅ In Stock (>= 10 items)
  - ⚠️ Low Stock (< 10 items)
  - ❌ Out of Stock (0 items)

### Customers Tab 👥
- **Add Customers**: ID, Name, Email
- **Customer Directory**: Complete customer list

### Suppliers Tab 🚛
- **Add Suppliers**: ID, Name, Contact
- **Supplier Database**: All supplier information

### Transactions Tab 💳
- **Record Sales**: Select product & customer, enter quantity
- **Stock Management**: Automatically updates inventory
- **Transaction History**: Complete transaction log

## 🔧 Database Configuration

**Default Settings** (in `DBConnection.java`):
```
Host: localhost:3306
Database: inventorydb  
Username: root
Password: (empty)
```

## 🆘 Troubleshooting

### ❌ Database Connection Issues
```
Error: Cannot connect to database
```
**Solution:**
1. Ensure XAMPP MySQL is running
2. Check database `inventorydb` exists
3. Verify tables imported from `db/inventory.sql`
4. Confirm JDBC driver in `lib/` folder

### 🔨 Compilation Errors
```
Error: Compilation failed
```
**Solution:**
1. Ensure Java JDK is installed (`java -version`)
2. Check MySQL JDBC driver in `lib/` folder
3. Run `setup-simple.bat` as administrator

### 🚫 Application Won't Start
```
Error: Application failed to run
```
**Solution:**
1. Run `setup-simple.bat` first
2. Check MySQL service is running in XAMPP
3. Verify database connection settings
4. Ensure all Java files compiled successfully

## 📋 Database Schema

### Tables:
- **`product`** - Product info (id, name, quantity, price)
- **`customer`** - Customer details (id, name, email)
- **`supplier`** - Supplier info (id, name, contact)
- **`transaction`** - Sales records (id, product_id, customer_id, quantity, date)

### Sample Data Included:
- 2 Products (Laptop, Mouse)
- 2 Customers (Rahul, Anjali)
- 2 Suppliers (Tech Supplies Ltd., Global Electronics)

## 🎨 UI Features

- **Modern Nimbus Look & Feel**
- **Color-coded Interface**: Blue, Green, Red, Orange themes
- **Professional Forms**: Clean input validation
- **Data Tables**: Sortable columns with proper formatting
- **Status Indicators**: Visual stock level indicators
- **Error Handling**: User-friendly error messages

## 🔄 How It Works

1. **Start Application** → Database connection tested
2. **Dashboard** → Shows overview statistics
3. **Add Data** → Products, customers, suppliers
4. **Record Transactions** → Select items, automatic stock update
5. **View Reports** → Real-time data in tables

---

## 👨‍💻 Technical Details

- **Language**: Java (Swing GUI)
- **Database**: MySQL with JDBC
- **Architecture**: Simple MVC pattern
- **Build**: No complex build tools needed
- **Dependencies**: Just MySQL JDBC driver

---

**💡 Simple, Clean & Ready to Use!**

*Built with ❤️ for easy learning and practical use*