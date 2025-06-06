package com.quickmarket.dao;

import com.quickmarket.model.Market;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarketDAO {
    private final Connection connection;

    public MarketDAO() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    public Market create(String name, String location) throws SQLException {
        String sql = "INSERT INTO market (name, location) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, location);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Market creation failed");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new Market(id, name, location);
                }
                throw new SQLException("No ID generated");
            }
        }
    }

    public List<Market> getAll() throws SQLException {
        List<Market> markets = new ArrayList<>();
        String sql = "SELECT * FROM market ORDER BY name ASC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                markets.add(mapMarket(rs));
            }
        }
        return markets;
    }

    public Market getById(int id) throws SQLException {
        String sql = "SELECT * FROM market WHERE market_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapMarket(rs);
                }
            }
        }
        return null;
    }

    public void updateMarket(int marketId, String name, String location) throws SQLException {
        String sql = "UPDATE market SET name = ?, location = ? WHERE market_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, location);
            stmt.setInt(3, marketId);
            stmt.executeUpdate();
        }
    }

    private Market mapMarket(ResultSet rs) throws SQLException {
        return new Market(rs.getInt("market_id"), rs.getString("name"), rs.getString("location"));
    }
}