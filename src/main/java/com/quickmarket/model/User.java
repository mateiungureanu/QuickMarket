package com.quickmarket.model;

public class User {
    private final int userId;
    private final String username;
    private final String email;
    private final String password;
    private final String userType;

    public User(int userId, String username, String email, String password, String userType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }
}
