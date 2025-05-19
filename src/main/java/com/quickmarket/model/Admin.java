package com.quickmarket.model;

public class Admin extends User {
    public Admin(int id, String username, String password, String email) {
        super(id, username, password, email);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
} 