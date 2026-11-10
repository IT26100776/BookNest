package com.booknest.demo.admin.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminLoginController {

    //Show the username of the admin
    @GetMapping("/api/admin/me")
    public Map<String, String> currentAdmin(Principal principal) {
        // Get the username from the current session.
        return Map.of("username", principal.getName());
    }
}
