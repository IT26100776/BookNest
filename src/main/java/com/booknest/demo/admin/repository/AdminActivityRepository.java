package com.booknest.demo.admin.repository;

import com.booknest.demo.admin.model.AdminActivity;
import com.booknest.demo.admin.model.AdminActivity.Action;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class AdminActivityRepository {

    private static final int RECENT_LIMIT = 100;

    private final Path activityFile;

    public AdminActivityRepository(
            @Value("${booknest.admin.activity-file:admin-activity.txt}") String filePath) {
        this.activityFile = Path.of(filePath);
    }

    public synchronized void append(AdminActivity activity) throws IOException {
        String line = String.join(",", activity.timestamp().toString(), activity.action().name(),
                activity.actor(), activity.accountId());
        Files.writeString(activityFile, line + "\n",
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public synchronized List<AdminActivity> findRecent() throws IOException {
        ArrayDeque<AdminActivity> recent = new ArrayDeque<>();
        try (BufferedReader reader = Files.newBufferedReader(activityFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                AdminActivity activity = parse(line);
                if (activity != null) {
                    recent.addFirst(activity);
                    if (recent.size() > RECENT_LIMIT) {
                        recent.removeLast();
                    }
                }
            }
        } catch (NoSuchFileException e) {
            return List.of();
        }
        return List.copyOf(recent);
    }

    private AdminActivity parse(String line) {
        String[] fields = line.split(",", -1);
        if (fields.length != 4) {
            return null;
        }
        try {
            return new AdminActivity(Instant.parse(fields[0]), Action.valueOf(fields[1]),
                    fields[2], fields[3]);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            // A partial line should not hide the other activity entries.
            return null;
        }
    }
}
