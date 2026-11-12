package com.booknest.demo.admin.model;

public abstract class User {
    //Subclasses use getters for these fields
    private String userId;
    private String username;
    private String password;
    private String email;

    //Default constructor
    public User() {
    }

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

    //Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //Each account type describes its role
    public abstract String getRoleDetails();
}
