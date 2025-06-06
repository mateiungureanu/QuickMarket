package com.quickmarket.service;

import com.quickmarket.dao.CustomerDAO;
import com.quickmarket.model.Customer;

import java.sql.SQLException;

public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService() throws SQLException {
        this.customerDAO = new CustomerDAO();
    }

    public Customer getById(int id) throws SQLException {
        return customerDAO.getById(id);
    }

    public void updateTotals(int customerId, double totalQuantity, double totalSpent) throws SQLException {
        customerDAO.updateTotals(customerId, totalQuantity, totalSpent);
    }

    public CustomerDAO getCustomerDAO() {
        return customerDAO;
    }
}
