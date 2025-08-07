package com.quickmarket.model;

import java.sql.Timestamp;

public class Alert {
    private final int id;
    private final int fromUserId;
    private final int toUserId;
    private final String productName;
    private final int productQuantity;
    private final String type;
    private boolean isCompleted;
    private boolean isResponded;
    private boolean isRead;
    private final Timestamp createdAt;

    public Alert(int id, int fromUserId, int toUserId, String productName, int productQuantity, 
                 String type, boolean isCompleted, boolean isResponded, boolean isRead, Timestamp createdAt) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.type = type;
        this.isCompleted = isCompleted;
        this.isResponded = isResponded;
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

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public boolean isResponded() {
        return isResponded;
    }

    public void setResponded(boolean responded) {
        this.isResponded = responded;
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