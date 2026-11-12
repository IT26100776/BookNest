package com.booknest.demo.admin.model;

public class AdminUser extends User {
    //A role change creates a new account object
    private final String adminLevel;
    private String permissions;

    //User sets the shared account fields
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

    //Describe the admin role
    @Override
    public String getRoleDetails() {
        return "System Administrator [" + adminLevel + "] - Permissions: " + permissions;
    }

    //Write one account record
    public String toFileString() {
        return getUserId() + "," + getUsername() + "," + getPassword() + ","
                + getEmail() + "," + adminLevel + "," + permissions;
    }

    //Choose the account type from its role
    public static AdminUser fromRole(String userId, String username, String passwordHash,
                                     String email, String adminLevel, String permissions) {
        if ("SUPER_ADMIN".equalsIgnoreCase(adminLevel)) {
            return new SuperAdminUser(userId, username, passwordHash, email, permissions);
        }
        return new AdminUser(userId, username, passwordHash, email, adminLevel, permissions);
    }

    //Read one account record
    public static AdminUser fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 6) {
            return fromRole(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    parts[3].trim(), parts[4].trim(), parts[5].trim());
        }
        return null;
    }
}
