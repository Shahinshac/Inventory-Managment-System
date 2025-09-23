package com.inventory.services;

import com.inventory.models.Product;
import com.inventory.exceptions.ProductNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product p) {
        products.add(p);
        System.out.println("Product added: " + p);
    }

    public void updateQuantity(int id, int newQty) throws ProductNotFoundException {
        for (Product p : products) {
            if (p.getId() == id) {
                p.setQuantity(newQty);
                return;
            }
        }
        throw new ProductNotFoundException("Product with ID " + id + " not found!");
    }

    public void displayProducts() {
        System.out.println("=== Product List ===");
        for (Product p : products) {
            System.out.println(p);
        }
    }
}
