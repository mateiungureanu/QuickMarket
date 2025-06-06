package com.quickmarket.model;

public class PurchaseHistory {
    private final int purchaseHistoryId;
    private final int customerId;
    private final int productId;
    private final double price;
    private int quantity;
    private final int stallId;
    private final String purchaseDate;

    public PurchaseHistory(int purchaseHistoryId, int customerId, int productId, double price, int quantity, int stallId, String purchaseDate) {
        this.purchaseHistoryId = purchaseHistoryId;
        this.customerId = customerId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.stallId = stallId;
        this.purchaseDate = purchaseDate;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getProductId() {
        return productId;
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

    public String getPurchaseDate() {
        return purchaseDate;
    }
} 