package com.quickmarket.model;

public class Product {
    private final int id;
    private String name;
    private double price;
    private int quantity;
    private final Stall stall;

    public Product(int id, String name, double price, int quantity, Stall stall) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.stall = stall;
    }

    public int getId() {
        return id;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Stall getStall() {
        return stall;
    }

    @Override
    public String toString() {
        return id +
                ". " + name +
                " - " + price +
                "$ (Quantity: " + quantity +
                ")";
    }
} 