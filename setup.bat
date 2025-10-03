@echo off
echo ==============================================
echo   📦 INVENTORY MANAGEMENT SYSTEM SETUP
echo ==============================================

echo.
echo 1. Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ERROR: Java not found! Please install Java 8 or higher.
    pause
    exit /b 1
)
echo ✅ Java found

echo.
echo 2. Creating directories...
if not exist "build" mkdir build
if not exist "lib" mkdir lib
echo ✅ Directories created

echo.
echo 3. Checking MySQL JDBC driver...
if not exist "lib\mysql-connector-java-*.jar" (
    echo ⚠️  WARNING: MySQL JDBC driver not found in lib folder!
    echo Please download mysql-connector-java-8.0.33.jar and place it in lib folder
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    echo.
    echo The application will not work without the JDBC driver!
    pause
) else (
    echo ✅ MySQL JDBC driver found
)

echo.
echo 4. Compiling Java files...
javac -cp "lib\*" -d build *.java
if %errorlevel% neq 0 (
    echo ❌ ERROR: Compilation failed! Check for errors above.
    pause
    exit /b 1
)
echo ✅ Compilation successful - All files compiled!

echo.
echo ==============================================
echo            🎉 SETUP COMPLETE! 🎉
echo ==============================================
echo.
echo 📋 Next Steps:
echo 1. Start XAMPP and start MySQL service
echo 2. Create database 'inventorydb' in phpMyAdmin  
echo 3. Import db/inventory.sql file
echo 4. Run: run.bat
echo.
echo 🚀 Ready to launch your Inventory Management System!
echo.
pause