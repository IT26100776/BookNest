package com.booknest.demo.admin.repository;

import com.booknest.demo.admin.model.AdminUser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class AdminFileRepository {

    private final Path adminFile;

    public AdminFileRepository(@Value("${booknest.admin.file:admins.txt}") String filePath) {
        this.adminFile = Path.of(filePath).toAbsolutePath();
    }

    public List<AdminUser> loadAll() throws IOException {
        List<AdminUser> accounts = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(adminFile)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                AdminUser admin = AdminUser.fromFileString(line);
                if (admin == null) {
                    throw new IOException("Invalid admin record at line " + lineNumber);
                }
                accounts.add(admin);
            }
        } catch (NoSuchFileException e) {
            // A fresh installation starts without an account file.
            return List.of();
        }
        return accounts;
    }

    public void saveAll(List<AdminUser> accounts) throws IOException {
        Path temporaryFile = Files.createTempFile(adminFile.getParent(), "admins-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile)) {
                for (AdminUser admin : accounts) {
                    writer.write(admin.toFileString());
                    writer.newLine();
                }
            }

            // Keep the previous account file until the new one is ready.
            Files.move(temporaryFile, adminFile,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException | RuntimeException e) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw e;
        }
    }
}
