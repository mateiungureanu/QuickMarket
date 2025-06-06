package com.quickmarket.dao;

import com.quickmarket.model.Stall;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StallDAO {
    private final Connection connection;

    public StallDAO() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    public Stall create(String name, int marketId, int ownerId) throws SQLException {
        String sql = "INSERT INTO stall (name, market_id, seller_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setInt(2, marketId);
            stmt.setInt(3, ownerId);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Stall creation failed");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new Stall(id, name, ownerId, marketId);
                }
                throw new SQLException("No ID generated");
            }
        }
    }

    public List<Stall> getByMarketId(int marketId) throws SQLException {
        List<Stall> stalls = new ArrayList<>();
        String sql = "SELECT * FROM stall WHERE market_id = ? ORDER BY name ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, marketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stalls.add(mapStall(rs));
                }
            }
        }
        return stalls;
    }

    public Stall getById(int stallId) throws SQLException {
        String sql = "SELECT * FROM stall WHERE stall_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, stallId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStall(rs);
                }
            }
        }
        return null;
    }

    public Stall getBySellerId(int sellerId) throws SQLException {
        String sql = "SELECT * FROM stall WHERE seller_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStall(rs);
                }
            }
        }
        return null;
    }

    private Stall mapStall(ResultSet rs) throws SQLException {
        return new Stall(rs.getInt("stall_id"), rs.getString("name"), rs.getInt("seller_id"), rs.getInt("market_id"));
    }
}