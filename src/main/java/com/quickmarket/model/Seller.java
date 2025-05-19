package com.quickmarket.model;

public class Seller extends User {
    private Stall stall;
    private double totalKilogramsSold;
    private double totalRevenue;
    public Seller(int id, String username, String password, String email) {
        super(id, username, password, email);
        this.totalKilogramsSold = 0.0;
        this.totalRevenue = 0;
    }

    public Stall getStall() {
        return stall;
    }

    public void setStall(Stall stall) {
        if (this.stall == null) {
            this.stall = stall;
        }
    }

    public double getTotalKilogramsSold() {
        return totalKilogramsSold;
    }

    public void setTotalKilogramsSold(double totalKilogramsSold) {
        this.totalKilogramsSold = totalKilogramsSold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", stall=" + (stall != null ? stall.getName() : "No stall") +
                ", totalKilogramsSold=" + totalKilogramsSold +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
} 