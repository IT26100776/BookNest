
package com.booknest.demo.usermanagement.service;

import com.booknest.demo.usermanagement.model.User;
import com.booknest.demo.usermanagement.model.UserRole;
import com.booknest.demo.usermanagement.model.UserView;
import com.booknest.demo.usermanagement.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Convert User into safe UserView
    private UserView toUserView(User user) {
        return new UserView(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    // 1. Register a new user
    public UserView registerUser(
            String name, String email, String password)
            throws IOException {

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "All fields are required.");
        }

        if (userRepository.findByEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException(
                    "Email already registered.");
        }

        String passwordHash = passwordEncoder.encode(password);

        User user = new User(
                UUID.randomUUID().toString(),
                name.trim(),
                email.trim().toLowerCase(),
                passwordHash,
                UserRole.CUSTOMER
        );

        userRepository.save(user);

        return toUserView(user);
    }

    // 2. Get all users
    public List<UserView> getAllUsers() throws IOException {

        return userRepository.findAll()
                .stream()
                .map(this::toUserView)
                .toList();
    }

    // 3. Get user by ID
    public UserView getUserById(String id) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."));

        return toUserView(user);
    }

    // 4. Get user by email
    public UserView getUserByEmail(String email)
            throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."));

        return toUserView(user);
    }

    // 5. Update user profile
    public UserView updateUser(
            String id, String name, String email)
            throws IOException {

        User currentUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."));

        if (name == null || name.isBlank()
                || email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Name and email are required.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        var existingUser =
                userRepository.findByEmail(normalizedEmail);

        if (existingUser.isPresent()
                && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    "Email is already used by another user.");
        }

        User updatedUser = currentUser.withUpdatedProfile(
                name.trim(),
                normalizedEmail
        );

        userRepository.save(updatedUser);

        return toUserView(updatedUser);
    }

    // 6. Delete user
    public boolean deleteUser(String id) throws IOException {

        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "User not found.");
        }

        return userRepository.deleteById(id);
    }
}
