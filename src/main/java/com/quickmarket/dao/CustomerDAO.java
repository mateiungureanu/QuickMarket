package com.quickmarket.dao;

import com.quickmarket.model.Customer;
import com.quickmarket.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final Connection connection;

    public CustomerDAO() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    public void create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (customer_id, total_quantity, total_spent) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customer.getUserId());
            stmt.setDouble(2, customer.getTotalQuantity());
            stmt.setDouble(3, customer.getTotalSpent());
            stmt.executeUpdate();
        }
    }

    public void updateTotals(int customerId, double totalQuantity, double totalSpent) throws SQLException {
        String sql = "UPDATE customer SET total_quantity = ?, total_spent = ? WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, totalQuantity);
            stmt.setDouble(2, totalSpent);
            stmt.setInt(3, customerId);
            stmt.executeUpdate();
        }
    }

    public Customer getById(int customerId) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.password, u.email, c.total_spent, c.total_quantity FROM user u JOIN customer c ON u.user_id = c.customer_id WHERE u.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    customer.setTotalSpent(rs.getDouble("total_spent"));
                    customer.setTotalQuantity(rs.getInt("total_quantity"));
                    return customer;
                }
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT u.*, c.total_spent, c.total_quantity FROM user u JOIN customer c ON u.user_id = c.customer_id WHERE u.user_type = 'customer'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("email"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                customer.setTotalQuantity(rs.getInt("total_quantity"));
                customers.add(customer);
            }
        }
        return customers;
    }
}