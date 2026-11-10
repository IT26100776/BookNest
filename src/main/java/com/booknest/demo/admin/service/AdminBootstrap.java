package com.booknest.demo.admin.service;

import com.booknest.demo.admin.model.AdminUser;
import java.io.Console;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("seed-admin")
public class AdminBootstrap implements ApplicationRunner {

    private final AdminManager adminManager;

    public AdminBootstrap(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    @Override
    public void run(ApplicationArguments args) {
        //Create the admin only when the account file is empty
        if (!adminManager.getAllAdmins().isEmpty()) {
            throw new IllegalStateException("The admin store already contains accounts.");
        }

        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("Run the seed command from an interactive terminal.");
        }

        String username = console.readLine("Admin username: ");
        String email = console.readLine("Admin email: ");
        //Hide the password while typing
        char[] password = console.readPassword("Admin password: ");
        if (password == null) {
            throw new IllegalStateException("A password is required.");
        }

        try {
            AdminUser admin = new AdminUser(
                    UUID.randomUUID().toString(), username, new String(password),
                    email, "SUPER_ADMIN", "ALL");
            //Hashes the password before saving the account
            if (!adminManager.addAdmin(admin)) {
                throw new IllegalStateException("The super admin could not be saved.");
            }
            console.printf("Super admin created.%n");
        } finally {
            Arrays.fill(password, (char) 0);
        }
    }
}
