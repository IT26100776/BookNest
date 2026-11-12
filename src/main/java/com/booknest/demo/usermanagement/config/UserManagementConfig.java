
package com.booknest.demo.usermanagement.config;

import com.booknest.demo.usermanagement.repository.FileUserRepository;
import com.booknest.demo.usermanagement.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class UserManagementConfig {

    @Bean
    public UserRepository userRepository() throws IOException {
        return new FileUserRepository("data/users.txt");
    }
}
