package com.booknest.demo.usermanagement.repository;

import com.booknest.demo.usermanagement.model.User;
import com.booknest.demo.usermanagement.model.UserRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Stores users in a pipe-separated text file: id|name|email|passwordHash|role */
public class FileUserRepository implements UserRepository {
    private final Path filePath;

    public FileUserRepository(String fileName) throws IOException {
        this(Paths.get(fileName));
    }

    public FileUserRepository(Path filePath) throws IOException {
        this.filePath = filePath.toAbsolutePath();
        Path parent = this.filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(this.filePath)) {
            Files.createFile(this.filePath);
        }
    }

    @Override
    public synchronized List<User> findAll() throws IOException {
        List<User> users = new ArrayList<User>();
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|", -1);
            if (parts.length != 5) {
                throw new IOException("Invalid user record at line " + lineNumber);
            }

            try {
                User user = new User(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        UserRole.valueOf(parts[4])
                );
                users.add(user);
            } catch (IllegalArgumentException ex) {
                throw new IOException("Invalid user role at line " + lineNumber, ex);
            }
        }
        return users;
    }

    @Override
    public synchronized Optional<User> findById(String id) throws IOException {
        for (User user : findAll()) {
            if (user.getId().equals(id)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<User> findByEmail(String email) throws IOException {
        for (User user : findAll()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /** Adds a new user, or replaces the stored user with the same ID. */
    @Override
    public synchronized void save(User user) throws IOException {
        List<User> users = findAll();
        boolean replaced = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            users.add(user);
        }
        writeAll(users);
    }

    @Override
    public synchronized boolean deleteById(String id) throws IOException {
        List<User> users = findAll();
        boolean removed = users.removeIf(user -> user.getId().equals(id));
        if (removed) {
            writeAll(users);
        }
        return removed;
    }

    private void writeAll(List<User> users) throws IOException {
        List<String> lines = new ArrayList<String>();
        for (User user : users) {
            validateFileField(user.getId());
            validateFileField(user.getName());
            validateFileField(user.getEmail());
            validateFileField(user.getPasswordHash());
            lines.add(user.getId() + "|" + user.getName() + "|"
                    + user.getEmail() + "|" + user.getPasswordHash() + "|"
                    + user.getRole().name());
        }

        // Write a temporary file first so a failed write is less likely to
        // leave users.txt half-written.
        Path temporaryFile = Files.createTempFile(
                filePath.getParent(), "users-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private void validateFileField(String value) {
        if (value == null || value.contains("|")
                || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(
                    "User fields cannot contain | or line breaks.");
        }
    }
}
