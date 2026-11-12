package com.booknest.demo.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminCreateRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank @Pattern(regexp = "ADMIN|SUPER_ADMIN") String adminLevel
) {
}