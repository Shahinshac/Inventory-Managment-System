package com.inventory.models;

import java.util.Date;

public class Transaction {
    private int id;
    private int productId;
    private int customerId;
    private int quantity;
    private Date date;

    public Transaction(int id, int productId, int customerId, int quantity, Date date) {
        this.id = id;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + id + " | Product: " + productId + " | Customer: " + customerId +
                " | Qty: " + quantity + " | Date: " + date;
    }
}
