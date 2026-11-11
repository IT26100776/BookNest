package com.booknest.demo.usermanagement.repository;

import com.booknest.demo.usermanagement.model.User;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** Abstraction for user storage. A database repository could replace the file one. */
public interface UserRepository {
    List<User> findAll() throws IOException;
    Optional<User> findById(String id) throws IOException;
    Optional<User> findByEmail(String email) throws IOException;
    void save(User user) throws IOException;
    boolean deleteById(String id) throws IOException;
}
