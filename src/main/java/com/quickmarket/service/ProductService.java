package com.quickmarket.service;

import com.quickmarket.dao.ProductDAO;
import com.quickmarket.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() throws SQLException {
        this.productDAO = new ProductDAO();
    }

    public void createProduct(String name, double price, int quantity, int stallId) throws SQLException {
        productDAO.create(name, price, quantity, stallId);
    }

    public void updateProduct(int productId, String name, double price, int quantity) throws SQLException {
        productDAO.update(productId, name, price, quantity);
    }

    public void deleteProduct(int productId) throws SQLException {
        productDAO.delete(productId);
    }

    public List<Product> getProductsByStall(int stallId) throws SQLException {
        return productDAO.getByStall(stallId);
    }

    public List<Product> getProductsByMarket(int marketId) throws SQLException {
        return productDAO.getByMarket(marketId);
    }

    public Product getProductById(int productId) throws SQLException {
        return getProductById(productId, false);
    }

    public Product getProductById(int productId, boolean includeDeleted) throws SQLException {
        return productDAO.getById(productId, includeDeleted);
    }
} 