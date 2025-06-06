package com.quickmarket.service;

import com.quickmarket.dao.MarketDAO;
import com.quickmarket.dao.ProductDAO;
import com.quickmarket.dao.StallDAO;
import com.quickmarket.model.Market;

import java.sql.SQLException;
import java.util.List;

public class MarketService {
    private final MarketDAO marketDAO;
    private final StallDAO stallDAO;
    private final ProductDAO productDAO;

    public MarketService() throws SQLException {
        this.marketDAO = new MarketDAO();
        this.stallDAO = new StallDAO();
        this.productDAO = new ProductDAO();
    }

    public void createMarket(String name, String location) throws SQLException {
        marketDAO.create(name, location);
    }

    public List<Market> getAllMarkets() throws SQLException {
        return marketDAO.getAll();
    }

    public void updateMarket(int marketId, String name, String location) throws SQLException {
        marketDAO.updateMarket(marketId, name, location);
    }

    public void viewAllMarkets() throws SQLException {
        List<Market> markets = marketDAO.getAll();
        if (markets.isEmpty()) {
            System.out.println("No markets available.");
            return;
        }
        System.out.println("\nAll Markets");
        for (Market market : markets) {
            System.out.println(market.getMarketId() + ". " + market.getName() + " - " + market.getLocation() + "\n    - Stalls: " + stallDAO.getByMarketId(market.getMarketId()).size() + "\n    - Products: " + productDAO.getByMarket(market.getMarketId()).size());
        }
    }

    public Market getMarketById(int marketId) throws SQLException {
        return marketDAO.getById(marketId);
    }
} 