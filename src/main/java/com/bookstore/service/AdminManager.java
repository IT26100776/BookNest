package com.bookstore.service;

import com.bookstore.model.AdminUser;
import java.io.*;
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
}