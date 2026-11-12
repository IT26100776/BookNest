package com.booknest.demo.admin.service;

import com.booknest.demo.admin.model.AdminActivity;
import com.booknest.demo.admin.model.AdminActivity.Action;
import com.booknest.demo.admin.repository.AdminActivityRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AdminActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminActivityService.class);

    private final AdminActivityRepository repository;

    public AdminActivityService(AdminActivityRepository repository) {
        this.repository = repository;
    }

    public void record(Action action, String actor, String accountId) {
        try {
            repository.append(new AdminActivity(Instant.now(), action, actor, accountId));
        } catch (IOException e) {
            //Warn if logging fails after the account is saved
            LOGGER.warn("Could not record admin account action {} for {}", action, accountId, e);
        }
    }

    public List<AdminActivity> findRecent() throws IOException {
        return repository.findRecent();
    }
}
