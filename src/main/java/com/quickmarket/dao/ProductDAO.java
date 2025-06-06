package com.quickmarket.dao;

import com.quickmarket.model.Product;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    public Product create(String name, double price, int quantity, int stallId) throws SQLException {
        String sql = "INSERT INTO product (name, price, quantity, stall_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.setInt(4, stallId);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Product creation failed");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new Product(id, name, price, quantity, stallId);
                }
                throw new SQLException("No ID generated");
            }
        }
    }

    public void update(int productId, String name, double price, int quantity) throws SQLException {
        String sql = "UPDATE product SET name = ?, price = ?, quantity = ? WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.setInt(4, productId);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Product update failed");
        }
    }

    public void delete(int productId) throws SQLException {
        String sql = "UPDATE product SET deleted = TRUE WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    public List<Product> getByStall(int stallId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product WHERE stall_id = ? AND deleted = FALSE";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, stallId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }
        }
        return products;
    }

    public List<Product> getByMarket(int marketId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, s.name as stall_name, " + "COALESCE(sl.quantity, 0) as shopping_list_quantity " + "FROM product p " + "JOIN stall s ON p.stall_id = s.stall_id " + "LEFT JOIN shopping_list sl ON p.product_id = sl.product_id " + "WHERE s.market_id = ? AND p.deleted = FALSE " + "ORDER BY p.name ASC, p.price ASC, p.quantity ASC, s.name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, marketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }
        }
        return products;
    }

    public List<Product> getAll() throws SQLException {
        String sql = "SELECT * FROM product WHERE deleted = FALSE";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        }
        return products;
    }

    public Product getById(int productId) throws SQLException {
        return getById(productId, false);
    }

    public Product getById(int productId, boolean includeDeleted) throws SQLException {
        String sql = "SELECT * FROM product WHERE product_id = ?" + (includeDeleted ? "" : " AND deleted = FALSE");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapProduct(rs);
            }
        }
        return null;
    }

    public void updateQuantity(int productId, int quantity) throws SQLException {
        String sql = "UPDATE product SET quantity = ? WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Product quantity update failed");
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("product_id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("quantity"), rs.getInt("stall_id"));
    }
}