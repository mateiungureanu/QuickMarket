package com.quickmarket.dao;

import com.quickmarket.model.Admin;
import com.quickmarket.model.Customer;
import com.quickmarket.model.Seller;
import com.quickmarket.model.User;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public int create(User user) throws SQLException {
        String sql = "INSERT INTO user (username, password, email, user_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getUserType());
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("User creation failed");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No ID generated");
            }
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    int userId = rs.getInt("user_id");
                    String email = rs.getString("email");

                    return switch (userType.toUpperCase()) {
                        case "CUSTOMER" -> new Customer(userId, username, email, password);
                        case "SELLER" -> new Seller(userId, username, email, password);
                        case "ADMIN" -> new Admin(userId, username, email, password);
                        default -> throw new SQLException("Invalid user type: " + userType);
                    };
                }
            }
        }
        return null;
    }

    public User register(String username, String email, String password, String type) throws SQLException {
        String sql = "INSERT INTO user (username, email, password, user_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, type.toLowerCase());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new User(id, username, email, password, type);
                } else {
                    return null;
                }
            }
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("user_type"));
    }

    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT u.*, c.total_quantity, c.total_spent FROM user u JOIN customer c ON u.user_id = c.customer_id WHERE u.user_type = 'CUSTOMER'";
        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = mapUser(rs);
                Customer customer = new Customer(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword());
                customer.setTotalQuantity(rs.getInt("total_quantity"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                customers.add(customer);
            }
        }
        return customers;
    }

    public List<Seller> getAllSellers() throws SQLException {
        String sql = "SELECT u.*, s.total_quantity, s.total_revenue FROM user u JOIN seller s ON u.user_id = s.seller_id WHERE u.user_type = 'SELLER'";
        List<Seller> sellers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = mapUser(rs);
                Seller seller = new Seller(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword());
                seller.setTotalQuantity(rs.getDouble("total_quantity"));
                seller.setTotalRevenue(rs.getDouble("total_revenue"));
                sellers.add(seller);
            }
        }
        return sellers;
    }

    public List<User> getAllUsersByType(String type) throws SQLException {
        String sql = "SELECT * FROM user WHERE user_type = ?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        }
        return users;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }
}