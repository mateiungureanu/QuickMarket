package com.quickmarket.model;

import java.util.*;

public class Stall {
    private final int id;
    private final String name;
    private final Seller owner;
    private final Set<Product> products;

    public Stall(int id, String name, Seller owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.products = new TreeSet<>(Comparator.comparing(Product::getName));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Seller getOwner() {
        return owner;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    @Override
    public String toString() {
        return "Stall{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner.getUsername() +
                ", products=" + products.size() +
                '}';
    }
} 