package com.booknest.demo.usermanagement.model;

/**
 * Represents a registered bookstore user.
 * The password is stored as a hash, never as plain text.
 */
public class User {
    private final String id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final UserRole role;

    public User(String id, String name, String email,
                String passwordHash, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    /** Internal use by the repository and authentication service only. */
    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    /** Returns a new User with changed profile data; keeps ID, hash and role. */
    public User withUpdatedProfile(String newName, String newEmail) {
        return new User(id, newName, newEmail, passwordHash, role);
    }

    /** Returns a new User with a new password hash; keeps everything else. */
    public User withPasswordHash(String newPasswordHash) {
        return new User(id, name, email, newPasswordHash, role);
    }

    /** Returns a new User with a different role; keeps everything else. */
    public User withRole(UserRole newRole) {
        return new User(id, name, email, passwordHash, newRole);
    }

    @Override
    public String toString() {
        // Deliberately do not print the password hash.
        return "User{id='" + id + "', name='" + name
                + "', email='" + email + "', role=" + role + "}";
    }
}
