package com.quickmarket;

import com.quickmarket.model.*;
import com.quickmarket.service.*;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String LOG_FILE = "actions_log.csv";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static User currentUser;
    private static UserService userService;
    private static AdminService adminService;
    private static MarketService marketService;
    private static StallService stallService;
    private static ProductService productService;
    private static ShoppingListService shoppingListService;
    private static PurchaseHistoryService purchaseHistoryService;
    private static AlertService alertService;
    private static com.quickmarket.utils.SocketClient socketClient;

    private static void logAction(String actionName) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            writer.append(actionName)
                  .append(",")
                  .append(timestamp)
                  .append("\n");
        } catch (IOException e) {
            System.err.println("Error writing to action log: " + e.getMessage());
        }
    }

    private static void initializeLogFile() {
        try {
            java.io.File file = new java.io.File(LOG_FILE);
            if (!file.exists() || file.length() == 0) {
                try (FileWriter writer = new FileWriter(LOG_FILE, false)) {
                    writer.append("Action,Timestamp\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing action log file: " + e.getMessage());
        }
    }

    private static void initializeServices() throws SQLException {
        userService = new UserService();
        adminService = new AdminService();
        marketService = new MarketService();
        stallService = new StallService();
        productService = new ProductService();
        shoppingListService = new ShoppingListService();
        purchaseHistoryService = new PurchaseHistoryService();
        alertService = new AlertService();
        
        initializeLogFile();
    }

    public static void main(String[] args) {
        try {
            initializeServices();
            
            while (true) {
                showMainMenu();
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 0 -> {
                        System.out.println("Goodbye!");
                        if (socketClient != null) {
                            socketClient.disconnect();
                        }
                        return;
                    }
                    case 1 -> handleLogin();
                    case 2 -> handleRegister();
                    case 3 -> handleAdminLogin();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error initializing services: " + e.getMessage());
        }
    }

    private static void showMainMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Admin login");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void showRegisterMenu() {
        System.out.println("\n1. Register as customer");
        System.out.println("2. Register as seller");
        System.out.println("0. Go back");
        System.out.print("Choose an option: ");
    }

    private static void showAdminMenu() {
        System.out.println("\n1. View all customers");
        System.out.println("2. View all sellers");
        System.out.println("3. View all markets");
        System.out.println("4. Add a market");
        System.out.println("5. Edit a market");
        System.out.println("0. Logout");
        System.out.print("Choose an option: ");
    }

    private static void showSellerMenu() {
        try {
            int unreadCount = alertService.getUnreadAlertCount(currentUser.getUserId());
            String alertOption = unreadCount > 0 ? "5. Alerts (" + unreadCount + ")" : "5. Alerts";
            System.out.println("\n1. View all products");
            System.out.println("2. Add a product");
            System.out.println("3. Edit a product");
            System.out.println("4. Delete a product");
            System.out.println(alertOption);
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
        } catch (SQLException e) {
            System.out.println("Error getting alert count: " + e.getMessage());
            System.out.println("\n1. View all products");
            System.out.println("2. Add a product");
            System.out.println("3. Edit a product");
            System.out.println("4. Delete a product");
            System.out.println("5. Alerts");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
        }
    }

    private static void showCustomerMenu() {
        try {
            int unreadCount = alertService.getUnreadAlertCount(currentUser.getUserId());
            String alertOption = unreadCount > 0 ? "4. Alerts (" + unreadCount + ")" : "4. Alerts";
            System.out.println("\n1. Choose Product");
            System.out.println("2. View shopping list");
            System.out.println("3. View purchase history");
            System.out.println(alertOption);
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
        } catch (SQLException e) {
            System.out.println("Error getting alert count: " + e.getMessage());
            System.out.println("\n1. Choose Product");
            System.out.println("2. View shopping list");
            System.out.println("3. View purchase history");
            System.out.println("4. Alerts");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
        }
    }

    private static void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = userService.login(username, password);
            if (user == null) {
                System.out.println("Login failed. Please try again.");
                return;
            }

            currentUser = user;
            System.out.println("Login successful!");
            
            logAction("User Login: " + username);

            try {
                socketClient = new com.quickmarket.utils.SocketClient(user.getUserId());
                if (socketClient.connect()) {
                    System.out.println("Connected to real-time alert system!");
                } else {
                    System.out.println("Warning: Could not connect to real-time alerts. Alerts will be available in the Alerts tab.");
                }
            } catch (Exception e) {
                System.out.println("Warning: Real-time alerts unavailable. Alerts will be available in the Alerts tab.");
            }

            switch (user.getUserType()) {
                case "ADMIN" -> handleAdminMenu();
                case "SELLER" -> handleSellerMenu();
                case "CUSTOMER" -> handleCustomerMenu();
                default -> {
                    System.out.println("Invalid user type. Logging out...");
                    currentUser = null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private static void handleRegister() {
        while (true) {
            showRegisterMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0 -> {
                    return;
                }
                case 1 -> {
                    handleCustomerRegistration();
                    return;
                }
                case 2 -> {
                    handleSellerRegistration();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleAdminLogin() throws SQLException {
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User admin = adminService.login("admin", password);
        if (admin != null) {
            System.out.println("\nLogin successful!");
            logAction("Admin Login");
            handleAdminMenu();
        } else {
            System.out.println("\nInvalid credentials!");
        }
    }

    private static void handleCustomerRegistration() {
        System.out.println("\nCustomer Registration");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = userService.register(username, email, password, "CUSTOMER");
            if (user == null) {
                System.out.println("Customer registration failed. Please try again.");
                return;
            }

            currentUser = user;
            System.out.println("Customer registered successfully!");
            logAction("Customer Registration: " + username);
            handleCustomerMenu();
        } catch (SQLException e) {
            System.out.println("Error during customer registration: " + e.getMessage());
        }
    }

    private static void handleSellerRegistration() {
        System.out.println("\nSeller Registration");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = userService.register(username, email, password, "SELLER");
            if (user == null) {
                System.out.println("Seller registration failed. Please try again.");
                return;
            }

            System.out.println("Seller registered successfully!");
            logAction("Seller Registration: " + username);
            currentUser = user;
            handleSellerMenu();
        } catch (SQLException e) {
            System.out.println("Error during seller registration: " + e.getMessage());
        }
    }

    private static void handleAdminMenu() throws SQLException {
        while (true) {
            showAdminMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 0:
                        if (socketClient != null) {
                            socketClient.disconnect();
                        }
                        logAction("Admin Logout");
                        currentUser = null;
                        return;
                    case 1:
                        handleViewCustomers();
                        break;
                    case 2:
                        handleViewSellers();
                        break;
                    case 3:
                        handleViewMarkets();
                        break;
                    case 4:
                        handleAddMarket();
                        break;
                    case 5:
                        handleUpdateMarket();
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleViewCustomers() throws SQLException {
        List<Customer> customers = adminService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("\nNo customers found.");
            return;
        }
        System.out.println("\nAll Customers");
        for (Customer customer : customers) {
            System.out.println(customer.getUserId() + ". " + customer.getUsername() + " - " + customer.getEmail());
            System.out.println("    - Total quantity: " + customer.getTotalQuantity());
            System.out.println("    - Total spent: $" + customer.getTotalSpent());
        }
    }

    private static void handleViewSellers() throws SQLException {
        List<Seller> sellers = adminService.getAllSellers();
        if (sellers.isEmpty()) {
            System.out.println("\nNo sellers found.");
            return;
        }
        System.out.println("\nAll Sellers");
        for (Seller seller : sellers) {
            Stall stall = stallService.getBySellerId(seller.getUserId());
            System.out.println(seller.getUserId() + ". " + seller.getUsername() + " - " + seller.getEmail() + " | " + (stall != null ? stall.getName() : "No stall"));
            System.out.println("    - Total quantity: " + seller.getTotalQuantity());
            System.out.println("    - Total revenue: $" + seller.getTotalRevenue());
        }
    }

    private static void handleViewMarkets() throws SQLException {
        List<Market> markets = marketService.getAllMarkets();
        if (markets.isEmpty()) {
            System.out.println("\nNo markets available.");
            return;
        }

        System.out.println("\nAvailable Markets");
        for (Market market : markets) {
            int stallCount = stallService.getByMarketId(market.getMarketId()).size();
            int productCount = productService.getProductsByMarket(market.getMarketId()).size();
            System.out.println(market.getMarketId() + ". " + market.getName() + " - " + market.getLocation());
            System.out.println("    - Stalls: " + stallCount);
            System.out.println("    - Products: " + productCount);
        }
    }

    private static void handleAddMarket() throws SQLException {
        System.out.println("\nAdd a Market");
        System.out.print("Enter market name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter market location: ");
        String location = scanner.nextLine();
        if (location.isEmpty()) {
            System.out.println("Location cannot be empty.");
            return;
        }

        marketService.createMarket(name, location);
        System.out.println("\nMarket added successfully!");
        logAction("Market Created: " + name + " at " + location);
    }

    private static void handleUpdateMarket() throws SQLException {
        List<Market> markets = marketService.getAllMarkets();
        if (markets.isEmpty()) {
            System.out.println("\nNo markets available.");
            return;
        }

        System.out.println("\nAll Markets");
        for (Market market : markets) {
            System.out.println(market.getMarketId() + ". " + market.getName() + " (" + market.getLocation() + ")");
        }

        System.out.print("\nEnter market ID (0 to go back): ");
        int marketId = scanner.nextInt();
        scanner.nextLine();
        if (marketId == 0) return;

        Market market = marketService.getMarketById(marketId);
        if (market == null) {
            System.out.println("Invalid market ID.");
            return;
        }

        System.out.println("\nEdit a Market");
        System.out.print("New name (" + market.getName() + "): ");
        String newName = scanner.nextLine();
        System.out.print("New location (" + market.getLocation() + "): ");
        String newLocation = scanner.nextLine();

        if (newName.isEmpty()) newName = market.getName();
        if (newLocation.isEmpty()) newLocation = market.getLocation();

        marketService.updateMarket(marketId, newName, newLocation);
        System.out.println("\nMarket updated successfully!");
        logAction("Market Updated: " + newName + " at " + newLocation);
    }

    private static void handleCustomerMenu() throws SQLException {
        while (true) {
            showCustomerMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 0:
                        if (socketClient != null) {
                            socketClient.disconnect();
                        }
                        logAction("Customer Logout: " + currentUser.getUsername());
                        currentUser = null;
                        return;
                    case 1:
                        handleChooseProduct();
                        break;
                    case 2:
                        handleShoppingList();
                        break;
                    case 3:
                        handlePurchaseHistory();
                        break;
                    case 4:
                        handleCustomerAlerts();
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleChooseProduct() throws SQLException {
        handleViewMarkets();

        System.out.print("\nSelect market ID (0 to go back): ");
        int marketId = scanner.nextInt();
        scanner.nextLine();
        if (marketId == 0) return;

        List<Product> products = productService.getProductsByMarket(marketId);
        if (products.isEmpty()) {
            System.out.println("\nNo products available in this market.");
            return;
        }

        System.out.println("\nAvailable Products");
        for (Product product : products) {
            Stall stall = stallService.getById(product.getStallId());
            int inList = shoppingListService.getQuantityInList(currentUser.getUserId(), product.getProductId());
            if (inList > 0) {
                System.out.println(product.getProductId() + ". " + product.getName() + " (" + product.getQuantity() + ") - $" + product.getPrice() + " | " + stall.getName() + " (already in list: " + inList + ")");
            } else {
                System.out.println(product.getProductId() + ". " + product.getName() + " (" + product.getQuantity() + ") - $" + product.getPrice() + " | " + stall.getName());
            }
        }

        System.out.println("\n0. Go back");
        System.out.println("00. Request a product");
        System.out.print("\nSelect product ID or option: ");
        String input = scanner.nextLine();
        
        if (input.equals("0")) return;
        if (input.equals("00")) {
            handleRequestProduct(marketId);
            return;
        }
        
        int productId;
        try {
            productId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        Product selectedProduct = products.stream().filter(p -> p.getProductId() == productId).findFirst().orElse(null);

        if (selectedProduct == null) {
            System.out.println("Invalid product selection.");
            return;
        }

        int currentQuantity = shoppingListService.getQuantityInList(currentUser.getUserId(), productId);
        if (currentQuantity > 0) {
            System.out.println("\nThis product is already in your shopping list with quantity: " + currentQuantity);
            System.out.print("Enter new total quantity (0 to remove from list): ");
        } else {
            System.out.print("Enter quantity: ");
        }

        int quantity = scanner.nextInt();
        scanner.nextLine();

        if (quantity == 0) {
            if (currentQuantity > 0) {
                shoppingListService.removeFromShoppingList(currentUser.getUserId(), productId);
                System.out.println("\nProduct removed from shopping list.");
                logAction("Product Removed from " + currentUser.getUsername() + "'s Shopping List:" + selectedProduct.getName());
            } else {
                System.out.println("Quantity must be greater than 0.");
            }
        } else if (quantity < 0) {
            System.out.println("Quantity must be greater than 0.");
        } else {
            if (currentQuantity > 0) {
                shoppingListService.updateQuantity(currentUser.getUserId(), productId, quantity);
                System.out.println("\nShopping list updated successfully!");
                logAction("Product Updated in : " + currentUser.getUsername() + "'s Shopping List:" + selectedProduct.getName() + ", quantity " + quantity);
            } else {
                shoppingListService.addToShoppingList(currentUser.getUserId(), productId, quantity);
                System.out.println("\nProduct added to shopping list successfully!");
                logAction("Product Added to " + currentUser.getUsername() + "'s Shopping List: " + selectedProduct.getName() + ", quantity " + quantity);
            }
        }
    }

    private static void handleShoppingList() throws SQLException {
        List<ShoppingList> items = shoppingListService.getShoppingList(currentUser.getUserId());
        if (items.isEmpty()) {
            System.out.println("\nYour shopping list is empty.");
            return;
        }

        System.out.println("\nShopping List");
        double total = 0;
        for (ShoppingList item : items) {
            Product product = productService.getProductById(item.getProductId());
            Stall stall = stallService.getById(product.getStallId());
            double itemTotal = product.getPrice() * item.getQuantity();
            total += itemTotal;

            if (product.getQuantity() < item.getQuantity()) {
                System.out.println(product.getProductId() + ". " + product.getName() + " (" + item.getQuantity() + ") - $" + product.getPrice() + " | " + stall.getName() + " (only " + product.getQuantity() + " left in stock)");
            } else {
                System.out.println(product.getProductId() + ". " + product.getName() + " (" + item.getQuantity() + ") - $" + product.getPrice() + " | " + stall.getName());
            }
        }

        System.out.println("\nTotal: $" + total);

        System.out.println("\n1. Finish Shopping");
        System.out.println("0. Go Back");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            shoppingListService.moveToPurchaseHistory(currentUser.getUserId());
            System.out.println("\nShopping completed successfully. Items have been moved to purchase history.");
            
            logAction(currentUser.getUsername() + "'s Shopping List Completed");
        }
    }

    private static void handlePurchaseHistory() throws SQLException {
        List<PurchaseHistory> history = purchaseHistoryService.getPurchaseHistory(currentUser.getUserId());
        if (history.isEmpty()) {
            System.out.println("\nNo purchase history available.");
            return;
        }

        System.out.println("\nPurchase History");
        for (PurchaseHistory item : history) {
            Product product = productService.getProductById(item.getProductId(), true);
            double total = item.getPrice() * item.getQuantity();
            System.out.println(product.getName() + " - Price: $" + item.getPrice() + " - Quantity: " + item.getQuantity() + " - Total: $" + total + " - Date: " + item.getPurchaseDate());
        }
    }

    private static void handleSellerMenu() throws SQLException {
        while (true) {
            showSellerMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 0:
                        if (socketClient != null) {
                            socketClient.disconnect();
                        }
                        logAction("Seller Logout: " + currentUser.getUsername());
                        currentUser = null;
                        return;
                    case 1:
                        handleViewSellerProducts();
                        break;
                    case 2:
                        handleAddProduct();
                        break;
                    case 3:
                        handleUpdateProduct();
                        break;
                    case 4:
                        handleDeleteProduct();
                        break;
                    case 5:
                        handleSellerAlerts();
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleViewSellerProducts() throws SQLException {
        Seller seller = (Seller) currentUser;
        Stall stall = stallService.getBySellerId(seller.getUserId());
        if (stall == null) {
            System.out.println("\nError: No stall found for this seller.");
            return;
        }

        List<Product> products = productService.getProductsByStall(stall.getStallId());
        if (products.isEmpty()) {
            System.out.println("\nNo products available in your stall.");
            return;
        }

        System.out.println("\nAll Products");
        for (Product product : products) {
            System.out.println(product.getProductId() + ". " + product.getName() + " (" + product.getQuantity() + ") - $" + product.getPrice());
        }
    }

    private static int handleSellerProducts() throws SQLException {
        handleViewSellerProducts();
        Seller seller = (Seller) currentUser;
        Stall stall = stallService.getBySellerId(seller.getUserId());

        System.out.print("\nEnter product ID (0 to go back): ");
        int productId = scanner.nextInt();
        scanner.nextLine();
        if (productId == 0) return 0;

        Product product = productService.getProductById(productId);
        if (product == null || product.getStallId() != stall.getStallId()) {
            System.out.println("Invalid product ID.");
        } else {
            return productId;
        }
        return 0;
    }

    private static void handleAddProduct() throws SQLException {
        Seller seller = (Seller) currentUser;
        Stall stall = stallService.getBySellerId(seller.getUserId());
        if (stall == null) {
            System.out.println("\nError: No stall found for this seller.");
            return;
        }

        System.out.println("\nAdd a Product");
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        if (price <= 0) {
            System.out.println("Price must be greater than 0.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }

        productService.createProduct(name, price, quantity, stall.getStallId());
        System.out.println("\nProduct added successfully!");
        
        logAction(currentUser.getUsername() + "'s Product Created: " + name);
    }

    private static void handleUpdateProduct() throws SQLException {
        int productId = handleSellerProducts();
        Product product = productService.getProductById(productId);

        System.out.println("\nEdit a Product");
        System.out.print("New name (" + product.getName() + "): ");
        String newName = scanner.nextLine();
        System.out.print("New price (" + product.getPrice() + "): ");
        String priceStr = scanner.nextLine();
        System.out.print("New quantity (" + product.getQuantity() + "): ");
        String quantityStr = scanner.nextLine();

        if (newName.isEmpty()) newName = product.getName();
        double newPrice = priceStr.isEmpty() ? product.getPrice() : Double.parseDouble(priceStr);
        int newQuantity = quantityStr.isEmpty() ? product.getQuantity() : Integer.parseInt(quantityStr);

        productService.updateProduct(productId, newName, newPrice, newQuantity);
        System.out.println("\nProduct updated successfully!");
        logAction(currentUser.getUsername() + "'s Product Updated: " + newName);
    }

    private static void handleDeleteProduct() throws SQLException {
        int productId = handleSellerProducts();

        Product product = productService.getProductById(productId);
        String productName = product != null ? product.getName() : "Unknown Product";

        productService.deleteProduct(productId);
        System.out.println("\nProduct deleted successfully!");
        logAction(currentUser.getUsername() + "'s Product Deleted: " + productName);
    }

    private static void handleCustomerAlerts() throws SQLException {
        List<Alert> alerts = alertService.getFilteredAlertsForCustomer(currentUser.getUserId());
        
        for (Alert alert : alerts) {
            if (!alert.isRead()) {
                alertService.markAlertAsRead(alert.getId());
            }
        }
        
        if (alerts.isEmpty()) {
            System.out.println("\nNo alerts available.");
            return;
        }
        
        System.out.println("\nAlerts");
        
        System.out.println("\n--- Your Requests ---");
        boolean hasOwnRequests = false;
        for (Alert alert : alerts) {
            if (alert.getType().equals("customer_to_seller") && alert.getFromUserId() == currentUser.getUserId()) {
                String status = alert.isCompleted() ? "[COMPLETED]" : "[UNCOMPLETED]";
                System.out.println(alert.getId() + ". " + status + " Request for " + alert.getProductQuantity() + " kg of " + alert.getProductName());
                hasOwnRequests = true;
            }
        }
        if (!hasOwnRequests) {
            System.out.println("No requests found.");
        }
        
        System.out.println("\n--- Seller Responses ---");
        boolean hasSellerResponses = false;
        for (Alert alert : alerts) {
            if (alert.getType().equals("seller_to_customer")) {
                Stall stall = stallService.getBySellerId(alert.getFromUserId());
                String stallName = (stall != null) ? stall.getName() : "Stall";
                String message = alertService.formatSellerResponseAlert(stallName, alert.getProductName(), alert.getProductQuantity());
                System.out.println(alert.getId() + ". " + message);
                hasSellerResponses = true;
            }
        }
        if (!hasSellerResponses) {
            System.out.println("No seller responses found.");
        }
        
        System.out.println("\n1. Mark a request as completed");
        System.out.println("0. Go back");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 0:
                return;
            case 1:
                handleMarkRequestCompleted();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleSellerAlerts() throws SQLException {
        List<Alert> alerts = alertService.getFilteredAlertsForSeller(currentUser.getUserId());
        
        for (Alert alert : alerts) {
            if (!alert.isRead()) {
                alertService.markAlertAsRead(alert.getId());
            }
        }
        
        if (alerts.isEmpty()) {
            System.out.println("\nNo customer requests available.");
            return;
        }
        
        System.out.println("\nCustomer Requests");
        for (Alert alert : alerts) {
            if (alert.getType().equals("customer_to_seller")) {
                String customerName = userService.getUserById(alert.getFromUserId()).getUsername();
                String message = alertService.formatCustomerRequestAlert(customerName, alert.getProductName(), alert.getProductQuantity());
                System.out.println(alert.getId() + ". " + message);
            }
        }
        
        System.out.println("\n1. Mark a request as responded");
        System.out.println("0. Go back");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 0:
                return;
            case 1:
                handleMarkRequestCompleted();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleMarkRequestCompleted() throws SQLException {
        if (currentUser.getUserType().equals("CUSTOMER")) {
            List<Alert> customerRequests = alertService.getFilteredAlertsForCustomer(currentUser.getUserId())
                .stream()
                .filter(alert -> alert.getType().equals("customer_to_seller") && 
                               alert.getFromUserId() == currentUser.getUserId() &&
                               !alert.isCompleted())
                .toList();
                
            if (customerRequests.isEmpty()) {
                System.out.println("No uncompleted requests found.");
                return;
            }
            
            System.out.println("\nYour Uncompleted Requests:");
            for (Alert alert : customerRequests) {
                System.out.println(alert.getId() + ". Request for " + alert.getProductQuantity() + " kg of " + alert.getProductName());
            }
            
            System.out.print("Enter request ID to mark as completed: ");
            int alertId = scanner.nextInt();
            scanner.nextLine();
            
            Alert alert = customerRequests.stream()
                .filter(a -> a.getId() == alertId)
                .findFirst()
                .orElse(null);
                
            if (alert == null) {
                System.out.println("Invalid request ID.");
                return;
            }
            
            alertService.markCustomerRequestAsCompleted(currentUser.getUserId(), alert.getProductName(), alert.getProductQuantity());
            System.out.println("\nRequest marked as completed!");
            logAction(currentUser.getUsername() + "'s Request Marked Completed: " + alert.getProductName());
            
        } else if (currentUser.getUserType().equals("SELLER")) {
            System.out.print("Enter alert ID to mark as responded: ");
            int alertId = scanner.nextInt();
            scanner.nextLine();
            
            Alert alert = alertService.getAlertById(alertId);
            if (alert == null || alert.getToUserId() != currentUser.getUserId()) {
                System.out.println("Invalid alert ID.");
                return;
            }
            
            try {
                int responseAlertId = alertService.markRequestAsResponded(currentUser.getUserId(), alertId);
                
                Stall stall = stallService.getBySellerId(currentUser.getUserId());
                String stallName = (stall != null) ? stall.getName() : "Stall";
                String message = alertService.formatSellerResponseAlert(stallName, alert.getProductName(), alert.getProductQuantity());
                
                boolean delivered = com.quickmarket.utils.AlertSender.sendAlert(alert.getFromUserId(), responseAlertId, message);
                if (delivered) {
                    alertService.markAlertAsRead(responseAlertId);
                }
                
                System.out.println("\nRequest marked as responded and customer notified!");
                logAction("Request Responded: " + alert.getProductName() + " by " + currentUser.getUsername());
                
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Cannot resolve: " + e.getMessage());
            }
        }
    }

    private static void handleRequestProduct(int marketId) throws SQLException {
        System.out.println("\nRequest a Product");
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        if (productName.isEmpty()) {
            System.out.println("Product name cannot be empty.");
            return;
        }
        System.out.print("Enter desired quantity (kg): ");
        int productQuantity = scanner.nextInt();
        scanner.nextLine();
        if (productQuantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }
        
        String customerName = currentUser.getUsername();
        List<Integer> alertIds = alertService.sendCustomerRequestWithNotificationInMarket(
            currentUser.getUserId(), customerName, productName, productQuantity, marketId);
        
        System.out.println("\nRequest sent to " + alertIds.size() + " relevant sellers in this market!");
        logAction("Product Requested: " + productName + ", quantity: " + productQuantity + " by " + currentUser.getUsername() + " in market " + marketId);
    }
}