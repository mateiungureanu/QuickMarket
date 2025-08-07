package com.quickmarket.dao;

import com.quickmarket.model.Alert;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {
    private final Connection connection;

    public AlertDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public int create(Alert alert) throws SQLException {
        String sql = "INSERT INTO alerts (from_user_id, to_user_id, product_name, product_quantity, type, is_completed, is_responded, is_read) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, alert.getFromUserId());
            stmt.setInt(2, alert.getToUserId());
            stmt.setString(3, alert.getProductName());
            stmt.setInt(4, alert.getProductQuantity());
            stmt.setString(5, alert.getType());
            stmt.setBoolean(6, alert.isCompleted());
            stmt.setBoolean(7, alert.isResponded());
            stmt.setBoolean(8, alert.isRead());
            
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Alert creation failed");
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No ID generated");
            }
        }
    }

    public List<Alert> getFilteredAlertsForCustomer(int customerId) throws SQLException {
        // Get customer's own requests (grouped to avoid duplicates) and seller responses only for uncompleted requests
        String sql = "SELECT DISTINCT a1.* FROM alerts a1 WHERE " +
                    "(a1.from_user_id = ? AND a1.type = 'customer_to_seller' AND a1.id = " +
                    "(SELECT MIN(a2.id) FROM alerts a2 WHERE a2.from_user_id = a1.from_user_id " +
                    "AND a2.product_name = a1.product_name AND a2.product_quantity = a1.product_quantity " +
                    "AND a2.type = 'customer_to_seller')) OR " +
                    "(a1.to_user_id = ? AND a1.type = 'seller_to_customer' AND " +
                    "EXISTS (SELECT 1 FROM alerts orig WHERE orig.from_user_id = ? AND orig.type = 'customer_to_seller' " +
                    "AND orig.product_name = a1.product_name AND orig.product_quantity = a1.product_quantity " +
                    "AND orig.is_completed = 0)) " +
                    "ORDER BY a1.created_at DESC";
        List<Alert> alerts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, customerId);
            stmt.setInt(3, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapAlert(rs));
                }
            }
        }
        return alerts;
    }

    public List<Alert> getFilteredAlertsForSeller(int sellerId) throws SQLException {
        String sql = "SELECT * FROM alerts WHERE to_user_id = ? AND type = 'customer_to_seller' " +
                    "AND is_completed = 0 AND is_responded = 0 " +
                    "ORDER BY created_at DESC";
        List<Alert> alerts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapAlert(rs));
                }
            }
        }
        return alerts;
    }

    public List<Alert> getUnreadAlertsForUser(int userId) throws SQLException {
        String sql = "SELECT * FROM alerts WHERE to_user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<Alert> alerts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapAlert(rs));
                }
            }
        }
        return alerts;
    }

    public int getUnreadAlertCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM alerts WHERE to_user_id = ? AND is_read = FALSE";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void markAsRead(int alertId) throws SQLException {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            stmt.executeUpdate();
        }
    }

    public void markCustomerRequestAsCompleted(int customerId, String productName, int productQuantity) throws SQLException {
        String sql = "UPDATE alerts SET is_completed = 1 WHERE from_user_id = ? AND product_name = ? AND product_quantity = ? AND type = 'customer_to_seller'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, productName);
            stmt.setInt(3, productQuantity);
            stmt.executeUpdate();
        }
    }

    public List<Alert> getCustomerRequestsForSeller(int sellerId) throws SQLException {
        String sql = "SELECT * FROM alerts WHERE to_user_id = ? AND type = 'customer_to_seller' AND is_completed = 0 ORDER BY created_at DESC";
        List<Alert> alerts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapAlert(rs));
                }
            }
        }
        return alerts;
    }

    public List<Alert> getSellerResponsesForCustomer(int customerId) throws SQLException {
        String sql = "SELECT * FROM alerts WHERE to_user_id = ? AND type = 'seller_to_customer' ORDER BY created_at DESC";
        List<Alert> alerts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapAlert(rs));
                }
            }
        }
        return alerts;
    }

    public Alert getAlertById(int alertId) throws SQLException {
        String sql = "SELECT * FROM alerts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAlert(rs);
                }
            }
        }
        return null;
    }

    public void markAsResponded(int alertId) throws SQLException {
        String sql = "UPDATE alerts SET is_responded = 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            stmt.executeUpdate();
        }
    }

    private Alert mapAlert(ResultSet rs) throws SQLException {
        return new Alert(
            rs.getInt("id"),
            rs.getInt("from_user_id"),
            rs.getInt("to_user_id"),
            rs.getString("product_name"),
            rs.getInt("product_quantity"),
            rs.getString("type"),
            rs.getBoolean("is_completed"),
            rs.getBoolean("is_responded"),
            rs.getBoolean("is_read"),
            rs.getTimestamp("created_at")
        );
    }
} 