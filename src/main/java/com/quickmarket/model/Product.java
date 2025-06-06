package com.quickmarket.model;

public class Product {
    private final int productId;
    private final int stallId;
    private String name;
    private final double price;
    private int quantity;
    private final boolean deleted;

    public Product(int productId, String name, double price, int quantity, int stallId) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.stallId = stallId;
        this.deleted = false;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStallId() {
        return stallId;
    }

    public boolean isDeleted() {
        return deleted;
    }
}