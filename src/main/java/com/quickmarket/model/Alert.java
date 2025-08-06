package com.quickmarket.model;

import java.sql.Timestamp;

public class Alert {
    private final int id;
    private final int fromUserId;
    private final int toUserId;
    private final String productName;
    private final int productQuantity;
    private final String type;
    private String statusCustomer;
    private String statusSeller;
    private boolean isRead;
    private final Timestamp createdAt;

    public Alert(int id, int fromUserId, int toUserId, String productName, int productQuantity, 
                 String type, String statusCustomer, String statusSeller, boolean isRead, Timestamp createdAt) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.type = type;
        this.statusCustomer = statusCustomer;
        this.statusSeller = statusSeller;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public String getType() {
        return type;
    }

    public String getStatusCustomer() {
        return statusCustomer;
    }

    public void setStatusCustomer(String statusCustomer) {
        this.statusCustomer = statusCustomer;
    }

    public String getStatusSeller() {
        return statusSeller;
    }

    public void setStatusSeller(String statusSeller) {
        this.statusSeller = statusSeller;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
} 