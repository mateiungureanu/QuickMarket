package com.quickmarket.dao;

import com.quickmarket.model.ShoppingList;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListDAO {
    private final Connection connection;

    public ShoppingListDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public int create(ShoppingList item) throws SQLException {
        String sql = "INSERT INTO shopping_list (customer_id, product_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getCustomerId());
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void delete(int shoppingListId) throws SQLException {
        String sql = "DELETE FROM shopping_list WHERE shopping_list_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, shoppingListId);
            stmt.executeUpdate();
        }
    }

    public List<ShoppingList> getByCustomerId(int customerId) throws SQLException {
        List<ShoppingList> items = new ArrayList<>();
        String sql = "SELECT * FROM shopping_list WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapShoppingList(rs));
                }
            }
        }
        return items;
    }

    public ShoppingList getById(int id) throws SQLException {
        String sql = "SELECT * FROM shopping_list WHERE shopping_list_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapShoppingList(rs);
                }
            }
        }
        return null;
    }

    public int getQuantityInList(int customerId, int productId) throws SQLException {
        String sql = "SELECT quantity FROM shopping_list WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }
        return 0;
    }

    public void deleteByCustomerAndProduct(int customerId, int productId) throws SQLException {
        String sql = "DELETE FROM shopping_list WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    public void updateQuantity(int customerId, int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE shopping_list SET quantity = ? WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, customerId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();
        }
    }

    private ShoppingList mapShoppingList(ResultSet rs) throws SQLException {
        return new ShoppingList(rs.getInt("shopping_list_id"), rs.getInt("customer_id"), rs.getInt("product_id"), rs.getInt("quantity"));
    }
}
