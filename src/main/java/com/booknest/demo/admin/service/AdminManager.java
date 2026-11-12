package com.booknest.demo.admin.service;

import com.booknest.demo.admin.model.AdminUser;
import com.booknest.demo.admin.repository.AdminFileRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminManager {

    public enum AddAdminResult {
        CREATED,
        DUPLICATE,
        INVALID_INPUT,
        SAVE_FAILED
    }
    public enum UpdateAdminStatus {
        UPDATED,
        NOT_FOUND,
        DUPLICATE,
        INVALID_INPUT,
        LAST_SUPER_ADMIN,
        SAVE_FAILED
    }

    public record UpdateAdminResult(UpdateAdminStatus status, AdminUser admin) {
    }

    public enum DeleteAdminResult {
        DELETED,
        NOT_FOUND,
        INVALID_INPUT,
        FORBIDDEN,
        SELF_DELETE,
        LAST_SUPER_ADMIN,
        SAVE_FAILED
    }

    //The repository handles file reads and writes
    private final AdminFileRepository repository;
    private final List<AdminUser> adminList;
    private final PasswordEncoder passwordEncoder;

    //Constructor
    public AdminManager(AdminFileRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.adminList = new ArrayList<>();
        loadAdminsFromFile();
    }

    public synchronized void loadAdminsFromFile() {
        try {
            List<AdminUser> loadedAdmins = repository.loadAll();
            adminList.clear();
            adminList.addAll(loadedAdmins);
        } catch (IOException e) {
            //Stop startup if the account file is unreadable
            throw new UncheckedIOException("Could not load admin accounts", e);
        }
    }

    private boolean saveAdminsToFile() {
        try {
            repository.saveAll(adminList);
            return true;
        } catch (IOException e) {
            //Return false so the caller can undo the change
            System.out.println("Error writing admin file: " + e.getMessage());
            return false;
        }
    }

    //Handle one account change at a time
    public synchronized AddAdminResult addAdmin(AdminUser newAdmin) {
        if (newAdmin == null
                || !isValidField(newAdmin.getUserId())
                || !isValidField(newAdmin.getUsername())
                || !isValidField(newAdmin.getEmail())
                || !isValidField(newAdmin.getAdminLevel())
                || !isValidField(newAdmin.getPermissions())) {
            return AddAdminResult.INVALID_INPUT;
        }

        //BCrypt has a 72-byte password limit
        String rawPassword = newAdmin.getPassword();
        if (rawPassword == null || rawPassword.isBlank()
                || rawPassword.getBytes(StandardCharsets.UTF_8).length > 72
                || rawPassword.indexOf(0) >= 0) {
            return AddAdminResult.INVALID_INPUT;
        }

        String userId = newAdmin.getUserId().trim();
        String username = newAdmin.getUsername().trim();
        if (getAdminById(userId) != null || findByUsername(username) != null) {
            return AddAdminResult.DUPLICATE;
        }

        String role = newAdmin.getAdminLevel().trim();
        if (!"ADMIN".equalsIgnoreCase(role) && !"SUPER_ADMIN".equalsIgnoreCase(role)) {
            return AddAdminResult.INVALID_INPUT;
        }

        //Hash the password before saving
        AdminUser storedAdmin = AdminUser.fromRole(
                userId, username, passwordEncoder.encode(rawPassword),
                newAdmin.getEmail().trim(), role, newAdmin.getPermissions().trim());

        adminList.add(storedAdmin);
        if (!saveAdminsToFile()) {
            //Remove the new account if saving fails
            adminList.remove(storedAdmin);
            return AddAdminResult.SAVE_FAILED;
        }
        return AddAdminResult.CREATED;
    }

    public synchronized UpdateAdminResult updateAdmin(
            String userId, String username, String email, String adminLevel, String permissions) {
        if (!isValidField(userId) || !isValidField(username) || !isValidField(email)
                || !isValidField(adminLevel) || !isValidField(permissions)) {
            return new UpdateAdminResult(UpdateAdminStatus.INVALID_INPUT, null);
        }

        String role = adminLevel.trim();
        if (!"ADMIN".equalsIgnoreCase(role) && !"SUPER_ADMIN".equalsIgnoreCase(role)) {
            return new UpdateAdminResult(UpdateAdminStatus.INVALID_INPUT, null);
        }

        AdminUser currentAdmin = getAdminById(userId.trim());
        if (currentAdmin == null) {
            return new UpdateAdminResult(UpdateAdminStatus.NOT_FOUND, null);
        }

        String updatedUsername = username.trim();
        AdminUser accountWithUsername = findByUsername(updatedUsername);
        if (accountWithUsername != null && accountWithUsername != currentAdmin) {
            return new UpdateAdminResult(UpdateAdminStatus.DUPLICATE, null);
        }

        //Keep at least one super admin
        if (isSuperAdmin(currentAdmin) && !"SUPER_ADMIN".equalsIgnoreCase(role)
                && adminList.stream().filter(this::isSuperAdmin).count() == 1) {
            return new UpdateAdminResult(UpdateAdminStatus.LAST_SUPER_ADMIN, null);
        }

        //Keep the account ID and password hash
        AdminUser updatedAdmin = AdminUser.fromRole(currentAdmin.getUserId(), updatedUsername,
                currentAdmin.getPassword(), email.trim(), role, permissions.trim());
        int index = adminList.indexOf(currentAdmin);
        adminList.set(index, updatedAdmin);

        if (!saveAdminsToFile()) {
            //Restore the old details if saving fails
            adminList.set(index, currentAdmin);
            return new UpdateAdminResult(UpdateAdminStatus.SAVE_FAILED, null);
        }

        return new UpdateAdminResult(UpdateAdminStatus.UPDATED, updatedAdmin);
    }

    public synchronized DeleteAdminResult deleteAdmin(String userId, String signedInAdminId) {
        if (!isValidField(userId)) {
            return DeleteAdminResult.INVALID_INPUT;
        }

        AdminUser signedInAdmin = getAdminById(signedInAdminId);
        if (signedInAdmin == null || !isSuperAdmin(signedInAdmin)) {
            return DeleteAdminResult.FORBIDDEN;
        }

        AdminUser adminToDelete = getAdminById(userId.trim());
        if (adminToDelete == null) {
            return DeleteAdminResult.NOT_FOUND;
        }

        //Use the account ID to block self-deletion
        if (adminToDelete == signedInAdmin) {
            return DeleteAdminResult.SELF_DELETE;
        }

        if (isSuperAdmin(adminToDelete)
                && adminList.stream().filter(this::isSuperAdmin).count() == 1) {
            return DeleteAdminResult.LAST_SUPER_ADMIN;
        }

        int index = adminList.indexOf(adminToDelete);
        adminList.remove(index);

        if (!saveAdminsToFile()) {
            //Restore the deleted account if saving fails
            adminList.add(index, adminToDelete);
            return DeleteAdminResult.SAVE_FAILED;
        }

        return DeleteAdminResult.DELETED;
    }

    private boolean isSuperAdmin(AdminUser admin) {
        String level = admin.getAdminLevel();
        return level != null && "SUPER_ADMIN".equalsIgnoreCase(level.trim());
    }

    private boolean isValidField(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        //Commas and line breaks break the file format
        return !value.contains(",") && !value.contains("\n") && !value.contains("\r");
    }

    //Return a copy of the account list
    public synchronized List<AdminUser> getAllAdmins() {
        return List.copyOf(adminList);
    }

    //Find an admin by username
    public synchronized AdminUser findByUsername(String username) {
        if (!isValidField(username)) {
            return null;
        }

        for (AdminUser admin : adminList) {
            if (admin.getUsername().equalsIgnoreCase(username.trim())) {
                return admin;
            }
        }
        return null;
    }

    //Find an admin by ID
    public synchronized AdminUser getAdminById(String userId) {
        for (AdminUser admin : adminList) {
            if (admin.getUserId().equalsIgnoreCase(userId)) {
                return admin;
            }
        }
        return null;
    }
}
