package com.bookstore.model;

public class AdminUser extends User {
    //Private attributes
    private String adminLevel;
    private String permissions;

    //Default constructor
    public AdminUser() {
        super();
    }

    //Parameterized constructor
    public AdminUser(String userId, String username, String password, String email, String adminLevel, String permissions) {
        super(userId, username, password, email);
        this.adminLevel = adminLevel;
        this.permissions = permissions;
    }

    //Getters
    public String getAdminLevel() {
        return adminLevel;
    }

    public String getPermissions() {
        return permissions;
    }

    //Setters
    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    //Display role details
    @Override
    public String getRoleDetails() {
        return "System Administrator [" + adminLevel + "] - Permissions: " + permissions;
    }
}