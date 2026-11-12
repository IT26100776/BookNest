package com.booknest.demo.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminUpdateRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "ADMIN|SUPER_ADMIN") String adminLevel
) {
}
