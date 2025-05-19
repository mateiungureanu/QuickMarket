package com.quickmarket.model;

import java.util.ArrayList;
import java.util.List;

public class Market {
    private final int id;
    private final String name;
    private final String location;
    private final List<Stall> stalls;

    public Market(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.stalls = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public List<Stall> getStalls() {
        return stalls;
    }

    public void addStall(Stall stall) {
        stalls.add(stall);
    }

    @Override
    public String toString() {
        return "Market{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", stalls=" + stalls.size() +
                '}';
    }
} 