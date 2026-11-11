package com.booknest.demo.admin.controller;

import com.booknest.demo.admin.dto.AdminResponse;
import com.booknest.demo.admin.service.AdminManager;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    private final AdminManager adminManager;

    public AdminLoginController(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    //Show the username of the admin
    @GetMapping("/me")
    public Map<String, String> currentAdmin(Principal principal) {
        //Get the username from the session
        return Map.of("username", principal.getName());
    }

    @GetMapping("/admins")
    public List<AdminResponse> listAdmins() {
        //Return the acc details needed for admin list
        return adminManager.getAllAdmins().stream()
                .map(admin -> new AdminResponse(
                        admin.getUserId(),
                        admin.getUsername(),
                        admin.getEmail(),
                        admin.getAdminLevel()))
                .toList();
    }
}