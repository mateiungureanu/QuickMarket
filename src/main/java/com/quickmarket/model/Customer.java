package com.quickmarket.model;

public class Customer extends User {
    private int totalQuantity;
    private double totalSpent;

    public Customer(int userId, String username, String email, String password) {
        super(userId, username, email, password, "CUSTOMER");
        this.totalQuantity = 0;
        this.totalSpent = 0.0;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
}