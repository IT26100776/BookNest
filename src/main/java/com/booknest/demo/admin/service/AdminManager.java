package com.booknest.demo.admin.service;

import com.booknest.demo.admin.model.AdminUser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class AdminManager {
    //Attributes
    private static final String FILE_PATH = "admins.txt";
    private List<AdminUser> adminList;

    //Constructor
    public AdminManager() {
        this.adminList = new ArrayList<>();
        loadAdminsFromFile();
    }

    //Load admins from text file
    public void loadAdminsFromFile() {
        adminList.clear();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
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
    public boolean addAdmin(AdminUser newAdmin) {
        if (newAdmin == null) {
            System.out.println("Error: Admin details are required!");
            return false;
        }

        if (!isValidField(newAdmin.getUserId()) ||
                !isValidField(newAdmin.getUsername()) ||
                !isValidField(newAdmin.getPassword()) ||
                !isValidField(newAdmin.getEmail()) ||
                !isValidField(newAdmin.getAdminLevel()) ||
                !isValidField(newAdmin.getPermissions())) {
            System.out.println("Error: Fill in all admin fields without commas or line breaks!");
            return false;
        }

        for (AdminUser admin : adminList) {
            if (admin.getUserId().equalsIgnoreCase(newAdmin.getUserId()) ||
                    admin.getUsername().equalsIgnoreCase(newAdmin.getUsername())) {
                System.out.println("Error: Admin ID or Username already exists!");
                return false;
            }
        }

        adminList.add(newAdmin);
        if (!saveAdminsToFile()) {
            //Remove the new admin if saving fails
            adminList.remove(newAdmin);
            return false;
        }
        return true;
    }

    public boolean updateAdmin(String userId, String username, String email,
                               String adminLevel, String permissions) {
        if (!isValidField(userId) || !isValidField(username) || !isValidField(email)
                || !isValidField(adminLevel) || !isValidField(permissions)) {
            System.out.println("Error: Fill in all admin fields without commas or line breaks!");
            return false;
        }

        AdminUser currentAdmin = getAdminById(userId.trim());
        if (currentAdmin == null) {
            System.out.println("Error: Admin not found!");
            return false;
        }

        String updatedUsername = username.trim();
        for (AdminUser admin : adminList) {
            if (admin != currentAdmin && admin.getUsername().trim().equalsIgnoreCase(updatedUsername)) {
                System.out.println("Error: Username already exists!");
                return false;
            }
        }

        //Keep the same ID and password
        AdminUser updatedAdmin = new AdminUser(currentAdmin.getUserId(), updatedUsername,
                currentAdmin.getPassword(), email.trim(), adminLevel.trim(), permissions.trim());

        int index = adminList.indexOf(currentAdmin);
        adminList.set(index, updatedAdmin);

        if (!saveAdminsToFile()) {
            //Keep the old details if saving fails
            adminList.set(index, currentAdmin);
            return false;
        }
        return true;
    }

    public boolean deleteAdmin(String userId, String signedInAdminId) {
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
    public List<AdminUser> getAllAdmins() {
        return adminList;
    }

    //Find admin by ID
    public AdminUser getAdminById(String userId) {
        for (AdminUser admin : adminList) {
            if (admin.getUserId().equalsIgnoreCase(userId)) {
                return admin;
            }
        }
        return null;
    }
}
