package com.quickmarket.model;

public class Stall {
    private final int stallId;
    private String name;
    private final int sellerId;
    private final int marketId;

    public Stall(int stallId, String name, int sellerId, int marketId) {
        this.stallId = stallId;
        this.name = name;
        this.sellerId = sellerId;
        this.marketId = marketId;
    }

    public int getStallId() {
        return stallId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSellerId() {
        return sellerId;
    }
}