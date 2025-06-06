package com.quickmarket.model;

public class Market {
    private final int marketId;
    private String name;
    private final String location;

    public Market(int marketId, String name, String location) {
        this.marketId = marketId;
        this.name = name;
        this.location = location;
    }

    public int getMarketId() {
        return marketId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }
}