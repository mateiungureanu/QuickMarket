package com.quickmarket.service;

import com.quickmarket.dao.AlertDAO;
import com.quickmarket.dao.SellerDAO;
import com.quickmarket.dao.StallDAO;
import com.quickmarket.dao.ProductDAO;
import com.quickmarket.model.Alert;
import com.quickmarket.model.Seller;
import com.quickmarket.model.Stall;
import com.quickmarket.model.Product;

import java.sql.SQLException;
import java.util.List;

public class AlertService {
    private final AlertDAO alertDAO;
    private final SellerDAO sellerDAO;
    private final StallDAO stallDAO;
    private final ProductDAO productDAO;

    public AlertService() throws SQLException {
        this.alertDAO = new AlertDAO();
        this.sellerDAO = new SellerDAO();
        this.stallDAO = new StallDAO();
        this.productDAO = new ProductDAO();
    }

    public List<Integer> sendCustomerRequest(int customerId, String productName, int productQuantity) throws SQLException {
        List<Seller> allSellers = sellerDAO.getAllSellers();
        List<Integer> alertIds = new java.util.ArrayList<>();
        
        for (Seller seller : allSellers) {
            Stall sellerStall = stallDAO.getBySellerId(seller.getUserId());
            if (sellerStall != null) {
                List<Product> sellerProducts = productDAO.getByStall(sellerStall.getStallId());
                boolean hasProduct = sellerProducts.stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(productName) && p.getQuantity() >= productQuantity);
                if (!hasProduct) {
                    Alert alert = new Alert(0, customerId, seller.getUserId(), productName, productQuantity, 
                                         "customer_to_seller", false, false, false, null);
                    int alertId = alertDAO.create(alert);
                    alertIds.add(alertId);
                }
            }
        }
        return alertIds;
    }
    
    public List<Integer> sendCustomerRequestWithNotification(int customerId, String customerName, String productName, int productQuantity) throws SQLException {
        List<Seller> allSellers = sellerDAO.getAllSellers();
        List<Integer> alertIds = new java.util.ArrayList<>();
        String message = formatCustomerRequestAlert(customerName, productName, productQuantity);
        
        for (Seller seller : allSellers) {
            Stall sellerStall = stallDAO.getBySellerId(seller.getUserId());
            if (sellerStall != null) {
                List<Product> sellerProducts = productDAO.getByStall(sellerStall.getStallId());
                boolean hasProduct = sellerProducts.stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(productName) && p.getQuantity() >= productQuantity);
                if (!hasProduct) {
                    Alert alert = new Alert(0, customerId, seller.getUserId(), productName, productQuantity, 
                                         "customer_to_seller", false, false, false, null);
                    int alertId = alertDAO.create(alert);
                    alertIds.add(alertId);
                    
                    boolean delivered = com.quickmarket.utils.AlertSender.sendAlert(seller.getUserId(), alertId, message);
                    if (delivered) {
                        alertDAO.markAsRead(alertId);
                    }
                }
            }
        }
        return alertIds;
    }

    public List<Integer> sendCustomerRequestWithNotificationInMarket(int customerId, String customerName, String productName, int productQuantity, int marketId) throws SQLException {
        List<Stall> stallsInMarket = stallDAO.getByMarketId(marketId);
        List<Integer> alertIds = new java.util.ArrayList<>();
        String message = formatCustomerRequestAlert(customerName, productName, productQuantity);
        
        for (Stall stall : stallsInMarket) {
            int sellerId = stall.getSellerId();
            List<Product> sellerProducts = productDAO.getByStall(stall.getStallId());
            boolean hasProduct = sellerProducts.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(productName) && p.getQuantity() >= productQuantity);
            if (!hasProduct) {
                Alert alert = new Alert(0, customerId, sellerId, productName, productQuantity, 
                                     "customer_to_seller", false, false, false, null);
                int alertId = alertDAO.create(alert);
                alertIds.add(alertId);
                
                boolean delivered = com.quickmarket.utils.AlertSender.sendAlert(sellerId, alertId, message);
                if (delivered) {
                    alertDAO.markAsRead(alertId);
                }
            }
        }
        return alertIds;
    }

    public int sendSellerResponse(int sellerId, int customerId, String productName, int productQuantity) throws SQLException {
        Alert alert = new Alert(0, sellerId, customerId, productName, productQuantity, 
                              "seller_to_customer", false, true, false, null);
        int alertId = alertDAO.create(alert);
        
        try {
            Stall sellerStall = stallDAO.getBySellerId(sellerId);
            String message = formatSellerResponseAlert(sellerStall.getName(), productName, productQuantity);
            boolean delivered = com.quickmarket.utils.AlertSender.sendAlert(customerId, alertId, message);
            if (delivered) {
                alertDAO.markAsRead(alertId);
            }
        } catch (Exception e) {
            System.err.println("Error sending real-time response alert: " + e.getMessage());
        }
        
        return alertId;
    }

    public List<Alert> getAlertsForUser(int userId) throws SQLException {
        return alertDAO.getAlertsForUser(userId);
    }

    public List<Alert> getFilteredAlertsForCustomer(int customerId) throws SQLException {
        return alertDAO.getFilteredAlertsForCustomer(customerId);
    }

    public List<Alert> getFilteredAlertsForSeller(int sellerId) throws SQLException {
        return alertDAO.getFilteredAlertsForSeller(sellerId);
    }

    public List<Alert> getUnreadAlertsForUser(int userId) throws SQLException {
        return alertDAO.getUnreadAlertsForUser(userId);
    }

    public int getUnreadAlertCount(int userId) throws SQLException {
        return alertDAO.getUnreadAlertCount(userId);
    }

    public void markAlertAsRead(int alertId) throws SQLException {
        alertDAO.markAsRead(alertId);
    }

    public void markCustomerRequestAsCompleted(int customerId, String productName, int productQuantity) throws SQLException {
        alertDAO.markCustomerRequestAsCompleted(customerId, productName, productQuantity);
    }

    public List<Alert> getCustomerRequestsForSeller(int sellerId) throws SQLException {
        return alertDAO.getCustomerRequestsForSeller(sellerId);
    }

    public List<Alert> getSellerResponsesForCustomer(int customerId) throws SQLException {
        return alertDAO.getSellerResponsesForCustomer(customerId);
    }

    public Alert getAlertById(int alertId) throws SQLException {
        return alertDAO.getAlertById(alertId);
    }

    public boolean verifySellerHasProduct(int sellerId, String productName, int productQuantity) throws SQLException {
        Stall sellerStall = stallDAO.getBySellerId(sellerId);
        if (sellerStall == null) {
            return false;
        }
        
        List<Product> sellerProducts = productDAO.getByStall(sellerStall.getStallId());
        return sellerProducts.stream()
            .anyMatch(p -> p.getName().equalsIgnoreCase(productName) && p.getQuantity() >= productQuantity);
    }

    public String formatCustomerRequestAlert(String customerName, String productName, int productQuantity) {
        return customerName + " wants to buy " + productQuantity + " kilos of " + productName + "!";
    }

    public String formatSellerResponseAlert(String stallName, String productName, int productQuantity) {
        return stallName + " has at least " + productQuantity + " kilos of " + productName + " in stock!";
    }

    public int markRequestAsResponded(int sellerId, int alertId) throws SQLException {
        Alert customerRequest = alertDAO.getAlertById(alertId);
        if (customerRequest == null || customerRequest.getToUserId() != sellerId) {
            throw new IllegalArgumentException("Alert not found or not for this seller");
        }

        if (!verifySellerHasProduct(sellerId, customerRequest.getProductName(), customerRequest.getProductQuantity())) {
            throw new IllegalStateException("Seller does not have enough " + customerRequest.getProductName() + " in stock");
        }

        alertDAO.markAsResponded(alertId);

        return sendSellerResponse(sellerId, customerRequest.getFromUserId(), 
                                customerRequest.getProductName(), customerRequest.getProductQuantity());
    }
} 