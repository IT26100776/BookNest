package com.booknest.demo.admin.model;

public abstract class User {
    //Subclasses use getters for these fields
    private String userId;
    private String username;
    private String password;
    private String email;

    //Constructor with account details
    public User(String userId, String username, String password, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    //Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    //Each account type describes its role
    public abstract String getRoleDetails();
}
