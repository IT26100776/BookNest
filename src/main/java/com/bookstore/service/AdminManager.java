package com.bookstore.service;

import com.bookstore.model.AdminUser;
import java.util.ArrayList;
import java.util.List;

public class AdminManager {
    //Attributes
    private static final String FILE_PATH = "admins.txt";
    private List<AdminUser> adminList;

    //Constructor
    public AdminManager() {
        this.adminList = new ArrayList<>();
    }
}