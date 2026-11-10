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

    //Convert object to text for admins.txt
    public String toFileString() {
        return userId + "," + username + "," + password + "," + email + "," + adminLevel + "," + permissions;
    }

    //Create object from text read from admins.txt
    public static AdminUser fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 6) {
            return new AdminUser(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim());
        }
        return null;
    }
}