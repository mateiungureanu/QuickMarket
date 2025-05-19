package com.quickmarket.service;

import com.quickmarket.model.*;

import java.util.*;

public class QuickMarketService {
    private final Map<String, User> users;
    private final Map<Integer, Market> markets;
    private final Map<Integer, Product> products;
    private User currentUser;

    public QuickMarketService() {
        this.users = new HashMap<>();
        this.markets = new HashMap<>();
        this.products = new HashMap<>();
        initializeDefaultAdmin();
    }

    private void initializeDefaultAdmin() {
        Admin admin = new Admin(1, "admin", "admin", "admin@quickmarket.com");
        users.put(admin.getUsername(), admin);
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void registerCustomer(String username, String password, String email) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        Customer customer = new Customer(users.size() + 1, username, password, email);
        users.put(username, customer);
    }

    public void registerSeller(String username, String password, String email, int marketId) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        Market market = markets.get(marketId);
        if (market == null) {
            throw new IllegalArgumentException("Market not found");
        }
        Seller seller = new Seller(users.size() + 1, username, password, email);
        Stall stall = new Stall(market.getStalls().size() + 1, username + "'s Stall", seller);
        market.addStall(stall);
        seller.setStall(stall);
        users.put(username, seller);
    }

    public void createMarket(String name, String location) {
        if (!(currentUser instanceof Admin)) {
            throw new IllegalStateException("Only admins can create markets");
        }
        Market market = new Market(markets.size() + 1, name, location);
        markets.put(market.getId(), market);
    }

    public List<Market> getAllMarkets() {
        return new ArrayList<>(markets.values());
    }

    public List<Stall> getStallsInMarket(int marketId) {
        Market market = markets.get(marketId);
        if (market == null) {
            throw new IllegalArgumentException("Market not found");
        }
        return market.getStalls();
    }

    public void addProductToStall(int stallId, String name, double price, int quantity) {
        if (!(currentUser instanceof Seller seller)) {
            throw new IllegalArgumentException("Only sellers can add products");
        }
        Stall stall = seller.getStall();
        if (stall == null || stall.getId() != stallId) {
            throw new IllegalArgumentException("Invalid stall");
        }
        Product product = new Product(products.size() + 1, name, price, quantity, stall);
        products.put(product.getId(), product);
        stall.addProduct(product);
    }

    public void modifyProduct(int productId, String name, double price, int quantity) {
        if (!(currentUser instanceof Seller seller)) {
            throw new IllegalArgumentException("Only sellers can modify products");
        }

        Product product = products.get(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (product.getStall().getOwner() != seller) {
            throw new IllegalArgumentException("You can only modify your own products");
        }

        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
    }

    public void deleteProduct(int productId) {
        if (!(currentUser instanceof Seller seller)) {
            throw new IllegalArgumentException("Only sellers can delete products");
        }

        Product product = products.get(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (product.getStall().getOwner() != seller) {
            throw new IllegalArgumentException("You can only delete your own products");
        }

        for (User user : users.values()) {
            if (user instanceof Customer customer) {
                List<ShoppingItem> shoppingList = customer.getShoppingList();
                shoppingList.removeIf(item -> item.getProduct().getId() == productId);
            }
        }

        product.getStall().removeProduct(product);
        products.remove(productId);
    }

    public List<Product> getProductsInStall(int stallId) {
        for (Market market : markets.values()) {
            for (Stall stall : market.getStalls()) {
                if (stall.getId() == stallId) {
                    return new ArrayList<>(stall.getProducts());
                }
            }
        }
        throw new IllegalArgumentException("Stall not found");
    }

    public List<ShoppingItem> getShoppingList() {
        if (!(currentUser instanceof Customer customer)) {
            throw new IllegalStateException("Only customers can view shopping list");
        }
        return customer.getShoppingList();
    }

    public void completePurchase() {
        if (!(currentUser instanceof Customer customer)) {
            throw new IllegalArgumentException("Only customers can complete purchases");
        }
        List<ShoppingItem> shoppingList = customer.getShoppingList();

        if (shoppingList.isEmpty()) {
            throw new IllegalArgumentException("Shopping list is empty");
        }

        List<ShoppingItem> purchasedItems = new ArrayList<>();
        
        for (ShoppingItem item : shoppingList) {
            Product product = item.getProduct();
            int availableQuantity = product.getQuantity();
            
            if (availableQuantity > 0) {
                int quantityToPurchase = Math.min(item.getQuantity(), availableQuantity);
                
                ShoppingItem purchasedItem = new ShoppingItem(product, quantityToPurchase);
                purchasedItems.add(purchasedItem);
                
                product.setQuantity(availableQuantity - quantityToPurchase);
                
                Seller seller = product.getStall().getOwner();
                seller.setTotalKilogramsSold(seller.getTotalKilogramsSold() + quantityToPurchase);
                seller.setTotalRevenue(seller.getTotalRevenue() + (product.getPrice() * quantityToPurchase));
            }
        }

        if (!purchasedItems.isEmpty()) {
            customer.addToPurchaseHistory(purchasedItems);
        }
        customer.clearShoppingList();
    }

    public List<Customer> getAllCustomers() {
        return users.values().stream()
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .toList();
    }

    public List<Seller> getAllSellers() {
        return users.values().stream()
                .filter(user -> user instanceof Seller)
                .map(user -> (Seller) user)
                .toList();
    }

    public void updateShoppingListItemQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        Customer customer = (Customer) currentUser;
        List<ShoppingItem> shoppingList = customer.getShoppingList();

        for (ShoppingItem item : shoppingList) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(quantity);
                return;
            }
        }

        shoppingList.add(new ShoppingItem(product, quantity));
    }

    public Product getProductById(int productId) {
        return products.get(productId);
    }
} 