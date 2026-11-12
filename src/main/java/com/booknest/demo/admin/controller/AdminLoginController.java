package com.booknest.demo.admin.controller;

import static com.booknest.demo.SecurityConfig.API_CSRF_TOKEN_ATTRIBUTE;

import com.booknest.demo.admin.dto.AdminCreateRequest;
import com.booknest.demo.admin.dto.AdminResponse;
import com.booknest.demo.admin.dto.AdminUpdateRequest;
import com.booknest.demo.admin.model.AdminActivity;
import com.booknest.demo.admin.model.AdminActivity.Action;
import com.booknest.demo.admin.model.AdminUser;
import com.booknest.demo.admin.service.AdminActivityService;
import com.booknest.demo.admin.service.AdminManager;
import com.booknest.demo.admin.service.AdminManager.UpdateAdminResult;
import com.booknest.demo.admin.service.AdminUserDetailsService.AdminPrincipal;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    private final AdminManager adminManager;
    private final AdminActivityService activityService;

    public AdminLoginController(AdminManager adminManager, AdminActivityService activityService) {
        this.adminManager = adminManager;
        this.activityService = activityService;
    }

    @GetMapping("/activity")
    public List<AdminActivity> recentActivity() throws IOException {
        return activityService.findRecent();
    }

    @GetMapping("/me")
    public Map<String, String> currentAdmin(@AuthenticationPrincipal AdminPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        //Find the signed-in admin by ID
        AdminUser admin = adminManager.getAdminById(principal.getUserId());
        if (admin == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return Map.of("username", admin.getUsername(), "adminLevel", admin.getAdminLevel());
    }

    @GetMapping({"/admins/csrf", "/csrf"})
    public Map<String, String> adminCsrfToken(
            @RequestAttribute(API_CSRF_TOKEN_ATTRIBUTE) CsrfToken csrfToken) {
        //The page sends this token with write requests
        return Map.of("headerName", csrfToken.getHeaderName(), "token", csrfToken.getToken());
    }

    //Load the accounts table
    @GetMapping("/admins")
    public List<AdminResponse> listAdmins() {
        return adminManager.getAllAdmins().stream()
                .map(this::toResponse)
                .toList();
    }

    //Receive the Add Admin form after security checks
    @PostMapping("/admins")
    public ResponseEntity<AdminResponse> createAdmin(
            @Valid @RequestBody AdminCreateRequest request, Principal principal) {
        String role = request.adminLevel();
        String userId = UUID.randomUUID().toString();

        //Set permissions based on the role
        String permissions = permissionsForRole(role);
        AdminUser newAdmin = new AdminUser(userId, request.username(), request.password(),
                request.email(), role, permissions);

        return switch (adminManager.addAdmin(newAdmin)) {
            case CREATED -> {
                //Log the change after saving
                activityService.record(Action.CREATED, principal.getName(), userId);
                yield ResponseEntity.status(HttpStatus.CREATED)
                        .body(new AdminResponse(userId, request.username().trim(),
                                request.email().trim(), role));
            }
            case DUPLICATE -> throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "An admin with this username or ID already exists.");
            case INVALID_INPUT -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "The admin account details are invalid.");
            //Return HTTP 500 if saving fails
            case SAVE_FAILED -> throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the admin account.");
        };
    }

    @PutMapping("/admins/{userId}")
    public AdminResponse updateAdmin(
            @PathVariable String userId, @Valid @RequestBody AdminUpdateRequest request,
            Principal principal) {
        String role = request.adminLevel();
        UpdateAdminResult result = adminManager.updateAdmin(userId, request.username(),
                request.email(), role, permissionsForRole(role));

        return switch (result.status()) {
            case UPDATED -> {
                activityService.record(Action.UPDATED, principal.getName(), result.admin().getUserId());
                yield toResponse(result.admin());
            }
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
            case DELETED -> {
                activityService.record(Action.DELETED, signedInAdmin.getUsername(), userId.trim());
                yield ResponseEntity.noContent().build();
            }
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
