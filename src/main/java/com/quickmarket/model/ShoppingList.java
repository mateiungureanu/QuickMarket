package com.quickmarket.model;

public class ShoppingList {
    private final int shoppingListId;
    private final int customerId;
    private final int productId;
    private int quantity;

    public ShoppingList(int shoppingListId, int customerId, int productId, int quantity) {
        this.shoppingListId = shoppingListId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getShoppingListId() {
        return shoppingListId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}