@echo off
echo ==============================================
echo   🚀 Running Inventory Management System
echo ==============================================

echo.
echo Starting GUI application...
java -cp "build;lib\*" InventoryGUI

if %errorlevel% neq 0 (
    echo.
    echo ❌ ERROR: Application failed to run!
    echo.
    echo 🔧 Troubleshooting:
    echo 1. Ensure MySQL is running (XAMPP)
    echo 2. Check database 'inventorydb' exists
    echo 3. Import db/inventory.sql if not done
    echo 4. Verify MySQL JDBC driver in lib folder
    echo 5. Run setup-final.bat if not done
    echo.
)

pause