package com.inventory;

import com.inventory.models.Product;
import com.inventory.services.InventoryService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Inventory Management System ===");

        InventoryService inventoryService = new InventoryService();

        // Example usage
        Product p1 = new Product(1, "Laptop", 10, 55000.0);
        inventoryService.addProduct(p1);

        inventoryService.displayProducts();
    }
}
