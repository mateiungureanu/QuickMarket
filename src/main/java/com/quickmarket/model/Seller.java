package com.quickmarket.model;

public class Seller extends User {
    private double totalQuantity;
    private double totalRevenue;

    public Seller(int userId, String name, String email, String password) {
        super(userId, name, email, password, "SELLER");
        this.totalQuantity = 0.0;
        this.totalRevenue = 0.0;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}