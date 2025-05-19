package com.quickmarket.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private final List<ShoppingItem> shoppingList;
    private final List<ShoppingItem> purchaseHistory;
    private double totalSpent;

    public Customer(int id, String username, String password, String email) {
        super(id, username, password, email);
        this.shoppingList = new ArrayList<>();
        this.purchaseHistory = new ArrayList<>();
        this.totalSpent = 0;
    }

    public List<ShoppingItem> getShoppingList() {
        return shoppingList;
    }

    public void clearShoppingList() {
        shoppingList.clear();
    }

    public List<ShoppingItem> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void addToPurchaseHistory(List<ShoppingItem> items) {
        purchaseHistory.addAll(items);
        for (ShoppingItem item : items) {
            totalSpent += item.getProduct().getPrice() * item.getQuantity();
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", totalSpent=" + totalSpent +
                '}';
    }
} 