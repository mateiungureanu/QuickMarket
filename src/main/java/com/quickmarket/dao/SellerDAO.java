package com.quickmarket.dao;

import com.quickmarket.model.Seller;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerDAO {
    private final Connection connection;

    public SellerDAO() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    public void create(Seller seller) throws SQLException {
        String sql = "INSERT INTO seller (seller_id, total_quantity, total_revenue) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, seller.getUserId());
            stmt.setDouble(2, seller.getTotalQuantity());
            stmt.setDouble(3, seller.getTotalRevenue());
            stmt.executeUpdate();
        }
    }

    public void updateTotals(int sellerId, double totalKg, double totalRevenue) throws SQLException {
        String sql = "UPDATE seller SET total_quantity = ?, total_revenue = ? WHERE seller_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, totalKg);
            stmt.setDouble(2, totalRevenue);
            stmt.setInt(3, sellerId);
            stmt.executeUpdate();
        }
    }

    public Seller getById(int sellerId) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.password, u.email, s.total_quantity, s.total_revenue FROM user u JOIN seller s ON u.user_id = s.seller_id WHERE u.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Seller seller = new Seller(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    seller.setTotalQuantity(rs.getDouble("total_quantity"));
                    seller.setTotalRevenue(rs.getDouble("total_revenue"));
                    return seller;
                }
            }
        }
        return null;
    }

    public List<Seller> getAllSellers() throws SQLException {
        List<Seller> sellers = new ArrayList<>();
        String sql = "SELECT u.*, s.total_revenue, s.total_quantity FROM user u JOIN seller s ON u.user_id = s.seller_id WHERE u.user_type = 'seller'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Seller seller = new Seller(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("email"));
                seller.setTotalRevenue(rs.getDouble("total_revenue"));
                seller.setTotalQuantity(rs.getDouble("total_quantity"));
                sellers.add(seller);
            }
        }
        return sellers;
    }
}