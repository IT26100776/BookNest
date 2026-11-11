package com.booknest.demo.admin.controller;

import com.booknest.demo.admin.dto.AdminCreateRequest;
import com.booknest.demo.admin.dto.AdminResponse;
import com.booknest.demo.admin.dto.AdminUpdateRequest;
import com.booknest.demo.admin.model.AdminUser;
import com.booknest.demo.admin.service.AdminManager;
import com.booknest.demo.admin.service.AdminManager.UpdateAdminResult;
import com.booknest.demo.admin.service.AdminUserDetailsService.AdminPrincipal;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    private final AdminManager adminManager;

    public AdminLoginController(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    @GetMapping("/me")
    public Map<String, String> currentAdmin(Principal principal) {
        //Get the username from the session
        return Map.of("username", principal.getName());
    }

    @GetMapping("/admins")
    public List<AdminResponse> listAdmins() {
        return adminManager.getAllAdmins().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/admins")
    public ResponseEntity<AdminResponse> createAdmin(
            @Valid @RequestBody AdminCreateRequest request) {
        String role = request.adminLevel();
        String userId = UUID.randomUUID().toString();

        //Set permissions on the server
        String permissions = permissionsForRole(role);
        AdminUser newAdmin = new AdminUser(userId, request.username(), request.password(),
                request.email(), role, permissions);

        return switch (adminManager.addAdmin(newAdmin)) {
            case CREATED -> ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AdminResponse(userId, request.username().trim(),
                            request.email().trim(), role));
            case DUPLICATE -> throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "An admin with this username or ID already exists.");
            case INVALID_INPUT -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "The admin account details are invalid.");
            case SAVE_FAILED -> throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the admin account.");
        };
    }

    @PutMapping("/admins/{userId}")
    public AdminResponse updateAdmin(
            @PathVariable String userId, @Valid @RequestBody AdminUpdateRequest request) {
        String role = request.adminLevel();
        UpdateAdminResult result = adminManager.updateAdmin(userId, request.username(),
                request.email(), role, permissionsForRole(role));

        return switch (result.status()) {
            case UPDATED -> toResponse(result.admin());
            case NOT_FOUND -> throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Admin account not found.");
            case DUPLICATE -> throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "An admin with this username already exists.");
            case INVALID_INPUT -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "The admin account details are invalid.");
            case LAST_SUPER_ADMIN -> throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "The last super admin cannot be demoted.");
            case SAVE_FAILED -> throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the admin account.");
        };
    }

    @DeleteMapping("/admins/{userId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String userId,
                                            @AuthenticationPrincipal AdminPrincipal signedInAdmin) {
        if (signedInAdmin == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return switch (adminManager.deleteAdmin(userId, signedInAdmin.getUserId())) {
            case DELETED -> ResponseEntity.noContent().build();
            case NOT_FOUND -> throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Admin account not found.");
            case INVALID_INPUT -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "The admin account ID is invalid.");
            case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            case SELF_DELETE -> throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You cannot delete your own admin account.");
            case LAST_SUPER_ADMIN -> throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "The last super admin cannot be deleted.");
            case SAVE_FAILED -> throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the admin account list.");
        };
    }

    private String permissionsForRole(String role) {
        return "SUPER_ADMIN".equals(role) ? "ALL" : "BOOKS_ORDERS";
    }

    private AdminResponse toResponse(AdminUser admin) {
        //Leave the password hash out of the response
        return new AdminResponse(
                admin.getUserId(), admin.getUsername(), admin.getEmail(), admin.getAdminLevel());
    }
}
