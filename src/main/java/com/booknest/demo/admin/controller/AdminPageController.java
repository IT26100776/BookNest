package com.booknest.demo.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin/dashboard.html")
    public String dashboard() {
        return "redirect:/admin.html";
    }
}
