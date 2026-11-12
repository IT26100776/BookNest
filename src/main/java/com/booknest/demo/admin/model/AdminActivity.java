package com.booknest.demo.admin.model;

import java.time.Instant;

//The actor made the change to this account ID
public record AdminActivity(Instant timestamp, Action action, String actor, String accountId) {

    public enum Action {
        CREATED,
        UPDATED,
        DELETED
    }
}
