package com.quickmarket;

import com.quickmarket.model.*;
import com.quickmarket.service.QuickMarketService;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;

public class Main {
    private static final QuickMarketService service = new QuickMarketService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            try {
                System.out.println("\n=== QuickMarket ===");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Admin Login");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> handleLogin();
                    case 2 -> handleRegistration();
                    case 3 -> handleAdminLogin();
                    case 4 -> running = false;
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleLogin() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = service.login(username, password);
            if (user != null) {
                if (user instanceof Customer) {
                    handleCustomerSession();
                } else if (user instanceof Seller) {
                    handleSellerSession();
                }
            } else {
                System.out.println("Invalid credentials.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login. Please try again.");
        }
    }

    private static void handleRegistration() {
        try {
            System.out.println("\n=== Registration ===");
            System.out.println("1. Register as Customer");
            System.out.println("2. Register as Seller");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();

            if (choice == 1) {
                service.registerCustomer(username, password, email);
                System.out.println("Customer registered successfully!");
            } else if (choice == 2) {
                List<Market> markets = service.getAllMarkets();
                if (markets.isEmpty()) {
                    System.out.println("No markets available. Please contact an admin.");
                    return;
                }
                System.out.println("\nAvailable Markets:");
                for (Market market : markets) {
                    System.out.println(market.getId() + ". " + market.getName());
                }
                System.out.print("Select market ID: ");
                int marketId = scanner.nextInt();
                scanner.nextLine();
                service.registerSeller(username, password, email, marketId);
                System.out.println("Seller registered successfully!");
            } else {
                System.out.println("Invalid option.");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred during registration. Please try again.");
        }
    }

    private static void handleAdminLogin() {
        try {
            System.out.print("Admin Password: ");
            String password = scanner.nextLine();
            User admin = service.login("admin", password);
            if (admin instanceof Admin) {
                handleAdminSession();
            } else {
                System.out.println("Invalid admin credentials.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during admin login. Please try again.");
        }
    }

    private static void handleCustomerSession() {
        boolean inSession = true;
        while (inSession) {
            try {
                System.out.println("\n=== Customer Menu ===");
                System.out.println("1. View Markets");
                System.out.println("2. View Shopping List");
                System.out.println("3. View Purchase History");
                System.out.println("4. Logout");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> handleMarketSelection();
                    case 2 -> handleShoppingList();
                    case 3 -> handlePurchaseHistory();
                    case 4 -> {
                        service.logout();
                        inSession = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
            }
        }
    }

    private static void handleMarketSelection() {
        try {
            List<Market> markets = service.getAllMarkets();
            if (markets.isEmpty()) {
                System.out.println("No markets available.");
                return;
            }

            System.out.println("\nAvailable Markets:");
            for (Market market : markets) {
                List<Stall> stalls = service.getStallsInMarket(market.getId());
                System.out.println(market.getId() + ". " + market.getName() + 
                                 " - " + market.getLocation() + 
                                 " (Stalls: " + stalls.size() + ")");
            }
            System.out.print("Select market ID: ");
            int marketId = scanner.nextInt();
            scanner.nextLine();

            handleStallSelection(marketId);
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void handleStallSelection(int marketId) {
        try {
            List<Stall> stalls = service.getStallsInMarket(marketId);
            if (stalls.isEmpty()) {
                System.out.println("No stalls available in this market.");
                return;
            }

            System.out.println("\nAvailable Stalls:");
            for (Stall stall : stalls) {
                System.out.println(stall.getId() + ". " + stall.getName() + 
                                 " (Products: " + stall.getProducts().size() + ")");
            }
            System.out.print("Select stall ID: ");
            int stallId = scanner.nextInt();
            scanner.nextLine();

            handleProductSelection(stallId);
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void handleProductSelection(int stallId) {
        try {
            List<Product> products = service.getProductsInStall(stallId);
            if (products.isEmpty()) {
                System.out.println("No products available in this stall.");
                return;
            }

            System.out.println("\nAvailable Products:");
            Customer customer = (Customer) service.getCurrentUser();
            List<ShoppingItem> shoppingList = customer.getShoppingList();
            
            for (Product product : products) {
                int quantityInList = 0;
                for (ShoppingItem item : shoppingList) {
                    if (item.getProduct().getId() == product.getId()) {
                        quantityInList = item.getQuantity();
                        break;
                    }
                }
                
                if (quantityInList > 0) {
                    System.out.printf("%d. %s - %.2f $ (Quantity: %d) (already in list: %d)%n",
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getQuantity(),
                            quantityInList);
                } else {
                    System.out.printf("%d. %s - %.2f $ (Quantity: %d)%n",
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getQuantity());
                }
            }

            System.out.println("\n1. Add to Shopping List");
            System.out.println("2. Back");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter product ID: ");
                    int productId = scanner.nextInt();
                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        service.updateShoppingListItemQuantity(productId, quantity);
                        System.out.println("Product added to shopping list.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 2 -> {
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        }
    }

    private static void handleShoppingList() {
        try {
            List<ShoppingItem> shoppingList = service.getShoppingList();
            if (shoppingList.isEmpty()) {
                System.out.println("Your shopping list is empty.");
                return;
            }

            System.out.println("\nShopping List:");
            double totalPrice = 0;
            for (ShoppingItem item : shoppingList) {
                Product product = item.getProduct();
                Stall stall = product.getStall();
                int availableQuantity = Math.min(item.getQuantity(), product.getQuantity());
                double itemTotal = product.getPrice() * availableQuantity;
                totalPrice += itemTotal;
                
                if (product.getQuantity() == 0) {
                    System.out.println(product.getName() + " - " + item.getQuantity() + 
                                     " * " + product.getPrice() + " $" +
                                     " = " + itemTotal + " $" +
                                     " (" + stall.getName() + ")" +
                                     " - No longer in stock");
                } else if (item.getQuantity() > product.getQuantity()) {
                    System.out.println(product.getName() + " - " + item.getQuantity() + 
                                     " * " + product.getPrice() + " $" +
                                     " = " + itemTotal + " $" +
                                     " (" + stall.getName() + ")" +
                                     " - Only " + product.getQuantity() + " available");
                } else {
                    System.out.println(product.getName() + " - " + item.getQuantity() + 
                                     " * " + product.getPrice() + " $" +
                                     " = " + itemTotal + " $" +
                                     " (" + stall.getName() + ")");
                }
            }
            System.out.println("\nTotal Price: " + totalPrice + " $");

            System.out.println("\n1. Complete Purchase");
            System.out.println("2. Back");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    try {
                        service.completePurchase();
                        System.out.println("Purchase completed successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 2 -> {
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        }
    }

    private static void handlePurchaseHistory() {
        try {
            Customer customer = (Customer) service.getCurrentUser();
            List<ShoppingItem> purchaseHistory = customer.getPurchaseHistory();
            if (purchaseHistory.isEmpty()) {
                System.out.println("No purchase history available.");
                return;
            }

            System.out.println("\nPurchase History:");
            for (ShoppingItem item : purchaseHistory) {
                Product product = item.getProduct();
                System.out.printf("%d. %s (Stall: %s) - %.2f $ x %d = %.2f $%n",
                        product.getId(),
                        product.getName(),
                        product.getStall().getName(),
                        product.getPrice(),
                        item.getQuantity(),
                        product.getPrice() * item.getQuantity());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while viewing purchase history.");
        }
    }

    private static void handleSellerSession() {
        boolean inSession = true;
        while (inSession) {
            try {
                System.out.println("\n=== Seller Menu ===");
                System.out.println("1. View Products");
                System.out.println("2. Add Product");
                System.out.println("3. Modify Product");
                System.out.println("4. Delete Product");
                System.out.println("5. Logout");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> handleSellerProducts();
                    case 2 -> handleAddProduct();
                    case 3 -> handleModifyProduct();
                    case 4 -> handleDeleteProduct();
                    case 5 -> {
                        service.logout();
                        inSession = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
            }
        }
    }

    private static void handleSellerProducts() {
        try {
            Seller seller = (Seller) service.getCurrentUser();
            Stall stall = seller.getStall();
            if (stall == null) {
                System.out.println("No stall assigned to you.");
                return;
            }

            List<Product> products = new ArrayList<>(stall.getProducts());
            if (products.isEmpty()) {
                System.out.println("No products in your stall.");
                return;
            }

            System.out.println("\nYour Products:");
            for (Product product : products) {
                System.out.println(product.getId() + ". " + product.getName() + 
                                 " - $" + product.getPrice() + 
                                 " (Quantity: " + product.getQuantity() + ")");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while viewing products.");
        }
    }

    private static void handleAddProduct() {
        try {
            System.out.print("Product Name: ");
            String name = scanner.nextLine();
            System.out.print("Price: ");
            double price = scanner.nextDouble();
            System.out.print("Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            try {
                Seller seller = (Seller) service.getCurrentUser();
                Stall stall = seller.getStall();
                if (stall == null) {
                    System.out.println("No stall assigned to you.");
                    return;
                }
                service.addProductToStall(stall.getId(), name, price, quantity);
                System.out.println("Product added successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number for price and quantity.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred while adding the product.");
        }
    }

    private static void handleModifyProduct() {
        try {
            Seller seller = (Seller) service.getCurrentUser();
            if (seller.getStall() == null || seller.getStall().getProducts().isEmpty()) {
                System.out.println("No products available to modify.");
                return;
            }

            System.out.println("\nYour Products:");
            Set<Product> products = seller.getStall().getProducts();
            for (Product product : products) {
                System.out.println(product);
            }

            System.out.print("Enter product ID to modify: ");
            int productId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("New Name: ");
            String name = scanner.nextLine();
            System.out.print("New Price: ");
            double price = scanner.nextDouble();
            System.out.print("New Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            try {
                service.modifyProduct(productId, name, price, quantity);
                System.out.println("Product modified successfully");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers for ID, price, and quantity.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred while modifying the product.");
        }
    }

    private static void handleDeleteProduct() {
        try {
            Seller seller = (Seller) service.getCurrentUser();
            if (seller.getStall() == null || seller.getStall().getProducts().isEmpty()) {
                System.out.println("No products available to delete.");
                return;
            }

            System.out.println("\nYour Products:");
            Set<Product> products = seller.getStall().getProducts();
            for (Product product : products) {
                System.out.println(product);
            }

            System.out.print("Enter product ID to delete: ");
            int productId = scanner.nextInt();
            scanner.nextLine();

            try {
                service.deleteProduct(productId);
                System.out.println("Product deleted successfully");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number for the product ID.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the product.");
        }
    }

    private static void handleAdminSession() {
        boolean inSession = true;
        while (inSession) {
            try {
                System.out.println("\n=== Admin Menu ===");
                System.out.println("1. View Customers");
                System.out.println("2. View Sellers");
                System.out.println("3. Create Market");
                System.out.println("4. Logout");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> handleViewCustomers();
                    case 2 -> handleViewSellers();
                    case 3 -> handleCreateMarket();
                    case 4 -> {
                        service.logout();
                        inSession = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
            }
        }
    }

    private static void handleViewCustomers() {
        try {
            List<Customer> customers = service.getAllCustomers();
            if (customers.isEmpty()) {
                System.out.println("No customers registered.");
                return;
            }

            System.out.println("\nCustomers:");
            for (Customer customer : customers) {
                System.out.println(customer.getId() + ". " + customer.getUsername() + 
                                 " - " + customer.getEmail() + 
                                 " (Purchases: " + customer.getPurchaseHistory().size() + ")");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred while viewing customers.");
        }
    }

    private static void handleViewSellers() {
        try {
            List<Seller> sellers = service.getAllSellers();
            if (sellers.isEmpty()) {
                System.out.println("No sellers registered.");
                return;
            }

            System.out.println("\nSellers:");
            for (Seller seller : sellers) {
                System.out.println(seller.getId() + ". " + seller.getUsername() + 
                                 " - " + seller.getEmail());
                Stall stall = seller.getStall();
                if (stall != null) {
                    System.out.println("   Stall: " + stall.getName() + 
                                     " (Products: " + stall.getProducts().size() + ")");
                }
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred while viewing sellers.");
        }
    }

    private static void handleCreateMarket() {
        try {
            System.out.print("Market Name: ");
            String name = scanner.nextLine();
            System.out.print("Location: ");
            String location = scanner.nextLine();

            try {
                service.createMarket(name, location);
                System.out.println("Market created successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while creating the market.");
        }
    }
}