package com.quickmarket.service;

import com.quickmarket.dao.SellerDAO;
import com.quickmarket.model.Seller;

import java.sql.SQLException;

public class SellerService {
    private final SellerDAO sellerDAO;

    public SellerService() throws SQLException {
        this.sellerDAO = new SellerDAO();
    }

    public Seller getById(int id) throws SQLException {
        return sellerDAO.getById(id);
    }

    public void updateTotals(int sellerId, double totalKg, double totalRevenue) throws SQLException {
        sellerDAO.updateTotals(sellerId, totalKg, totalRevenue);
    }

    public SellerDAO getSellerDAO() {
        return sellerDAO;
    }
} 