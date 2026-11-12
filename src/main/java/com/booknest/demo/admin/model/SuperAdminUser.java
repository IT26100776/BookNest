package com.booknest.demo.admin.model;

public final class SuperAdminUser extends AdminUser {

    SuperAdminUser(String userId, String username, String passwordHash,
                   String email, String permissions) {
        super(userId, username, passwordHash, email, "SUPER_ADMIN", permissions);
    }

    @Override
    public String getRoleDetails() {
        return super.getRoleDetails() + " - Can manage admin accounts";
    }
}
