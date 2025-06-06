package com.quickmarket.service;

import com.quickmarket.dao.UserDAO;
import com.quickmarket.model.Customer;
import com.quickmarket.model.Seller;
import com.quickmarket.model.User;

import java.sql.SQLException;
import java.util.List;

public class AdminService {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String ADMIN_EMAIL = "admin@quickmarket.com";
    private final UserDAO userDAO;

    public AdminService() throws SQLException {
        this.userDAO = new UserDAO();
        ensureAdminExists();
    }

    private void ensureAdminExists() throws SQLException {
        User admin = userDAO.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        if (admin == null) {
            userDAO.register(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, "ADMIN");
        }
    }

    public User login(String username, String password) throws SQLException {
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            return userDAO.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return userDAO.getAllCustomers();
    }

    public List<Seller> getAllSellers() throws SQLException {
        return userDAO.getAllSellers();
    }
} 