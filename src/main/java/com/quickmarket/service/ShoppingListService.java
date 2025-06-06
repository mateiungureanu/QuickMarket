package com.quickmarket.service;

import com.quickmarket.dao.ShoppingListDAO;
import com.quickmarket.model.Product;
import com.quickmarket.model.ShoppingList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListService {
    private final ShoppingListDAO shoppingListDAO;
    private final ProductService productService;
    private final PurchaseHistoryService purchaseHistoryService;

    public ShoppingListService() throws SQLException {
        this.shoppingListDAO = new ShoppingListDAO();
        this.productService = new ProductService();
        this.purchaseHistoryService = new PurchaseHistoryService();
    }

    public void addToShoppingList(int customerId, int productId, int quantity) throws SQLException {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (product.isDeleted()) {
            throw new IllegalArgumentException("Product is no longer available");
        }

        ShoppingList item = new ShoppingList(0, customerId, productId, quantity);
        shoppingListDAO.create(item);
    }

    public List<ShoppingList> getShoppingList(int customerId) throws SQLException {
        List<ShoppingList> items = shoppingListDAO.getByCustomerId(customerId);
        List<ShoppingList> validItems = new ArrayList<>();
        boolean hasDeletedItems = false;

        for (ShoppingList item : items) {
            Product product = productService.getProductById(item.getProductId());
            if (product != null && !product.isDeleted()) {
                validItems.add(item);
            } else {
                hasDeletedItems = true;
                shoppingListDAO.delete(item.getShoppingListId());
            }
        }

        if (hasDeletedItems) {
            System.out.println("\nNote: Some items were removed from your shopping list because they are no longer available.");
        }

        return validItems;
    }

    public void moveToPurchaseHistory(int customerId) throws SQLException {
        List<ShoppingList> items = getShoppingList(customerId);

        for (ShoppingList item : items) {
            Product product = productService.getProductById(item.getProductId());
            if (product != null && !product.isDeleted()) {
                int availableQuantity = Math.min(item.getQuantity(), product.getQuantity());
                if (availableQuantity > 0) {
                    purchaseHistoryService.addToPurchaseHistory(customerId, item.getProductId(), availableQuantity);
                }
            }
        }

        for (ShoppingList item : items) {
            shoppingListDAO.delete(item.getShoppingListId());
        }
    }

    public int getQuantityInList(int customerId, int productId) throws SQLException {
        return shoppingListDAO.getQuantityInList(customerId, productId);
    }

    public void removeFromShoppingList(int customerId, int productId) throws SQLException {
        shoppingListDAO.deleteByCustomerAndProduct(customerId, productId);
    }

    public void updateQuantity(int customerId, int productId, int newQuantity) throws SQLException {
        shoppingListDAO.updateQuantity(customerId, productId, newQuantity);
    }
} 