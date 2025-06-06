package com.quickmarket.service;

import com.quickmarket.dao.StallDAO;
import com.quickmarket.model.Stall;

import java.sql.SQLException;
import java.util.List;

public class StallService {
    private final StallDAO stallDAO;

    public StallService() throws SQLException {
        this.stallDAO = new StallDAO();
    }

    public Stall create(String name, int marketId, int ownerId) throws SQLException {
        return stallDAO.create(name, marketId, ownerId);
    }

    public Stall getById(int stallId) throws SQLException {
        return stallDAO.getById(stallId);
    }

    public Stall getBySellerId(int sellerId) throws SQLException {
        return stallDAO.getBySellerId(sellerId);
    }

    public List<Stall> getByMarketId(int marketId) throws SQLException {
        return stallDAO.getByMarketId(marketId);
    }
} 