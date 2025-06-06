package com.quickmarket.service;

import com.quickmarket.dao.PurchaseHistoryDAO;
import com.quickmarket.model.*;

import java.sql.SQLException;
import java.util.List;

public class PurchaseHistoryService {
    private final PurchaseHistoryDAO purchaseHistoryDAO;
    private final ProductService productService;
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final StallService stallService;

    public PurchaseHistoryService() throws SQLException {
        this.purchaseHistoryDAO = new PurchaseHistoryDAO();
        this.productService = new ProductService();
        this.customerService = new CustomerService();
        this.sellerService = new SellerService();
        this.stallService = new StallService();
    }

    public void addToPurchaseHistory(int customerId, int productId, int quantity) throws SQLException {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        PurchaseHistory purchaseHistory = new PurchaseHistory(0, customerId, productId, product.getPrice(), quantity, product.getStallId(), null);
        purchaseHistoryDAO.create(purchaseHistory);

        Customer customer = customerService.getById(customerId);
        customerService.updateTotals(customerId, customer.getTotalQuantity() + quantity, customer.getTotalSpent() + (product.getPrice() * quantity));

        Stall stall = stallService.getById(product.getStallId());
        if (stall != null) {
            Seller seller = sellerService.getById(stall.getSellerId());
            if (seller != null) {
                sellerService.updateTotals(seller.getUserId(), seller.getTotalQuantity() + quantity, seller.getTotalRevenue() + (product.getPrice() * quantity));
            }
        }
    }

    public List<PurchaseHistory> getPurchaseHistory(int customerId) throws SQLException {
        return purchaseHistoryDAO.getByCustomerId(customerId);
    }
} 