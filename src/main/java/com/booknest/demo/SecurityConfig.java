package com.booknest.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // TEMPORARY (testing only): disable CSRF so Postman POST/PUT/DELETE work
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Lets real error messages through instead of redirecting to /login
                        .requestMatchers("/error").permitAll()

                        // TEMPORARY (testing only): every API module is open
                        // (books, orders, reviews, cart, ...)
                        .requestMatchers("/api/**").permitAll()

                        // Everything else still requires login
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}