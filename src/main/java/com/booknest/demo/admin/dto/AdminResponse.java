package com.booknest.demo.admin.dto;

public record AdminResponse(
        String userId,
        String username,
        String email,
        String adminLevel
) {
}