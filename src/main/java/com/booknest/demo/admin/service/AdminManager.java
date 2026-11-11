package com.booknest.demo.admin.service;

import com.booknest.demo.admin.model.AdminUser;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    //Attributes
    private static final String FILE_PATH = "admins.txt";
    private final List<AdminUser> adminList;
    private final PasswordEncoder passwordEncoder;

    //Constructor
    public AdminManager(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.adminList = new ArrayList<>();
        loadAdminsFromFile();
    }

    //Load admins from text file
    public synchronized void loadAdminsFromFile() {
        adminList.clear();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    AdminUser admin = AdminUser.fromFileString(line);
                    if (admin != null) {
                        adminList.add(admin);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing reader: " + e.getMessage());
            }
        }
    }

    //Save admins to txt
    private boolean saveAdminsToFile() {
        Path temporaryFile = null;
        try {
            Path adminFile = Path.of(FILE_PATH).toAbsolutePath();
            temporaryFile = Files.createTempFile(adminFile.getParent(), "admins-", ".tmp");

            //Write admin to the temporary file
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile)) {
                for (AdminUser admin : adminList) {
                    writer.write(admin.toFileString());
                    writer.newLine();
                }
            }

            Files.move(temporaryFile, adminFile,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
            return false;
        } finally {
            try {
                if (temporaryFile != null) {
                    Files.deleteIfExists(temporaryFile);
                }
            } catch (IOException e) {
                System.out.println("Error removing temporary file: " + e.getMessage());
            }
        }
    }

    //Add new admin
    public synchronized AddAdminResult addAdmin(AdminUser newAdmin) {
        if (newAdmin == null
                || !isValidField(newAdmin.getUserId())
                || !isValidField(newAdmin.getUsername())
                || !isValidField(newAdmin.getEmail())
                || !isValidField(newAdmin.getAdminLevel())
                || !isValidField(newAdmin.getPermissions())) {
            return AddAdminResult.INVALID_INPUT;
        }

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

        AdminUser storedAdmin = new AdminUser(
                userId, username, passwordEncoder.encode(rawPassword),
                newAdmin.getEmail().trim(), role, newAdmin.getPermissions().trim());

        adminList.add(storedAdmin);
        if (!saveAdminsToFile()) {
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

        // Keep at least one super admin in the account store.
        if (isSuperAdmin(currentAdmin) && !"SUPER_ADMIN".equalsIgnoreCase(role)
                && adminList.stream().filter(this::isSuperAdmin).count() == 1) {
            return new UpdateAdminResult(UpdateAdminStatus.LAST_SUPER_ADMIN, null);
        }

        // Keep the existing ID and password hash when editing an account.
        AdminUser updatedAdmin = new AdminUser(currentAdmin.getUserId(), updatedUsername,
                currentAdmin.getPassword(), email.trim(), role, permissions.trim());
        int index = adminList.indexOf(currentAdmin);
        adminList.set(index, updatedAdmin);

        if (!saveAdminsToFile()) {
            adminList.set(index, currentAdmin);
            return new UpdateAdminResult(UpdateAdminStatus.SAVE_FAILED, null);
        }

        return new UpdateAdminResult(UpdateAdminStatus.UPDATED, updatedAdmin);
    }

    public synchronized boolean deleteAdmin(String userId, String signedInAdminId) {
        if (!isValidField(userId) || !isValidField(signedInAdminId)) {
            System.out.println("Error: Both admin IDs are required!");
            return false;
        }

        AdminUser adminToDelete = getAdminById(userId.trim());
        if (adminToDelete == null) {
            System.out.println("Error: Admin not found!");
            return false;
        }

        if (adminToDelete.getUserId().trim().equalsIgnoreCase(signedInAdminId.trim())) {
            System.out.println("Error: You cannot delete your own account!");
            return false;
        }

        if (isSuperAdmin(adminToDelete)) {
            int superAdminCount = 0;
            for (AdminUser admin : adminList) {
                if (isSuperAdmin(admin)) {
                    superAdminCount++;
                }
            }

            if (superAdminCount == 1) {
                System.out.println("Error: You cannot delete the last super admin!");
                return false;
            }
        }

        int index = adminList.indexOf(adminToDelete);
        adminList.remove(index);

        if (!saveAdminsToFile()) {
            //Add the admin back if saving fails
            adminList.add(index, adminToDelete);
            return false;
        }
        return true;
    }

    private boolean isSuperAdmin(AdminUser admin) {
        String level = admin.getAdminLevel();
        return level != null && "SUPER_ADMIN".equalsIgnoreCase(level.trim());
    }

    private boolean isValidField(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        //Comma separated fields with ","
        return !value.contains(",") && !value.contains("\n") && !value.contains("\r");
    }

    //Get all admins
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

    //Find admin by ID
    public synchronized AdminUser getAdminById(String userId) {
        for (AdminUser admin : adminList) {
            if (admin.getUserId().equalsIgnoreCase(userId)) {
                return admin;
            }
        }
        return null;
    }
}
