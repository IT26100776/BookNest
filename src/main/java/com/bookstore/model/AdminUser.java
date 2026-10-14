package com.bookstore.model;

public class AdminUser extends User {
    //Protected attributes
    private String adminLevel;

    //Default constructor
    public AdminUser() {
        super();
    }

    //Parameterized constructor
    public AdminUser(String userId, String username, String password, String email, String adminLevel) {
        super(userId, username, password, email);
        this.adminLevel = adminLevel;
    }

    //Getters
    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    //Display role details
    @Override
    public String getRoleDetails() {
        return "Admin Level: " + adminLevel;
    }
}