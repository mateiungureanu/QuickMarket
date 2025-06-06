package com.quickmarket.service;

import com.quickmarket.dao.UserDAO;
import com.quickmarket.model.Customer;
import com.quickmarket.model.Market;
import com.quickmarket.model.Seller;
import com.quickmarket.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserService {
    private final UserDAO userDAO;
    private final StallService stallService;
    private final MarketService marketService;
    private final SellerService sellerService;
    private final CustomerService customerService;
    private final Scanner scanner;

    public UserService() throws SQLException {
        this.userDAO = new UserDAO();
        this.stallService = new StallService();
        this.marketService = new MarketService();
        this.sellerService = new SellerService();
        this.customerService = new CustomerService();
        this.scanner = new Scanner(System.in);
    }

    public User login(String email, String password) throws SQLException {
        return userDAO.login(email, password);
    }

    public User register(String name, String email, String password, String type) throws SQLException {
        if (type.equals("SELLER")) {
            List<Market> markets = marketService.getAllMarkets();
            if (markets.isEmpty()) {
                System.out.println("\nNo markets available. Please try again later.");
                return null;
            }
        }

        User user = userDAO.register(name, email, password, type);
        if (user == null) {
            return null;
        }

        if (type.equals("SELLER")) {
            System.out.println("\nAvailable Markets:");
            marketService.viewAllMarkets();

            System.out.print("\nSelect market ID for your stall: ");
            int marketId = scanner.nextInt();
            scanner.nextLine();

            Seller seller = new Seller(user.getUserId(), name, email, password);
            sellerService.getSellerDAO().create(seller);

            stallService.create(name + "'s Stall", marketId, user.getUserId());
            return seller;
        } else if (type.equals("CUSTOMER")) {
            Customer customer = new Customer(user.getUserId(), name, email, password);
            customerService.getCustomerDAO().create(customer);
            return customer;
        }
        return user;
    }
}
