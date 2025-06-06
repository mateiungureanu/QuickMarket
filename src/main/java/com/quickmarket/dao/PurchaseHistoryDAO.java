package com.quickmarket.dao;

import com.quickmarket.model.PurchaseHistory;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryDAO {
    private final Connection connection;

    public PurchaseHistoryDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public int create(PurchaseHistory purchaseHistory) throws SQLException {
        String sql = "INSERT INTO purchase_history (customer_id, product_id, price, quantity, stall_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, purchaseHistory.getCustomerId());
            stmt.setInt(2, purchaseHistory.getProductId());
            stmt.setDouble(3, purchaseHistory.getPrice());
            stmt.setInt(4, purchaseHistory.getQuantity());
            stmt.setInt(5, purchaseHistory.getStallId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<PurchaseHistory> getByCustomerId(int customerId) throws SQLException {
        List<PurchaseHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM purchase_history WHERE customer_id = ? ORDER BY purchase_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapPurchaseHistory(rs));
                }
            }
        }
        return history;
    }

    private PurchaseHistory mapPurchaseHistory(ResultSet rs) throws SQLException {
        return new PurchaseHistory(rs.getInt("purchase_history_id"), rs.getInt("customer_id"), rs.getInt("product_id"), rs.getDouble("price"), rs.getInt("quantity"), rs.getInt("stall_id"), rs.getString("purchase_date"));
    }
} 