
package com.booknest.demo.usermanagement.controller;

import com.booknest.demo.usermanagement.model.UserView;
import com.booknest.demo.usermanagement.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Request DTO for registration
    public record RegisterUserRequest(
            String name,
            String email,
            String password
    ) {}

    // Request DTO for profile updates
    public record UpdateUserRequest(
            String name,
            String email
    ) {}

    // 1. Register a new user
    @PostMapping
    public ResponseEntity<UserView> registerUser(
            @RequestBody RegisterUserRequest request)
            throws IOException {

        UserView user = userService.registerUser(
                request.name(),
                request.email(),
                request.password()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    // 2. Get all users
    @GetMapping
    public ResponseEntity<List<UserView>> getAllUsers()
            throws IOException {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // 3. Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserView> getUserById(
            @PathVariable String id)
            throws IOException {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    // 4. Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserView> getUserByEmail(
            @PathVariable String email)
            throws IOException {

        return ResponseEntity.ok(
                userService.getUserByEmail(email)
        );
    }

    // 5. Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<UserView> updateUser(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request)
            throws IOException {

        UserView updatedUser = userService.updateUser(
                id,
                request.name(),
                request.email()
        );

        return ResponseEntity.ok(updatedUser);
    }

    // 6. Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id)
            throws IOException {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    // Handle validation and not-found errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException ex) {

        if ("User not found.".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }

        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    // Handle file storage errors
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleStorageError(
            IOException ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "User storage operation failed."));
    }
}
