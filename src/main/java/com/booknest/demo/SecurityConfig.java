package com.booknest.demo;

import com.booknest.demo.admin.model.AdminUser;
import com.booknest.demo.admin.service.AdminManager;
import com.booknest.demo.admin.service.AdminUserDetailsService.AdminPrincipal;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AdminManager adminManager)
            throws Exception {
        http.addFilterBefore(new AdminSessionFilter(adminManager), AuthorizationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/shop.html",
                                "/book.html", "/css/**", "/img/**", "/js/main.js", "/js/vendor/**")
                        .permitAll()
                        .requestMatchers("/admin/accounts.html", "/admin/accounts.js",
                                "/admin/activity.html", "/admin/activity.js",
                                "/api/admin/activity", "/api/admin/admins", "/api/admin/admins/**")
                        .hasRole("SUPER_ADMIN")
                        .requestMatchers("/admin/dashboard.html", "/admin.html", "/js/admin.js",
                                "/js/admin-navigation.js", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**")
                        .permitAll()
                        .requestMatchers("/api/books", "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("ADMIN")
                        .requestMatchers("/api/orders", "/api/orders/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews", "/api/reviews/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews").permitAll()
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    private static final class AdminSessionFilter extends OncePerRequestFilter {

        private final AdminManager adminManager;
        private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

        private AdminSessionFilter(AdminManager adminManager) {
            this.adminManager = adminManager;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.getPrincipal() instanceof AdminPrincipal principal) {
                AdminUser currentAdmin = adminManager.getAdminById(principal.getUserId());
                if (accountChanged(currentAdmin, principal)) {
                    // Require a fresh login after a rename, role change, or deletion.
                    logoutHandler.logout(request, response, authentication);
                }
            }
            filterChain.doFilter(request, response);
        }

        private boolean accountChanged(AdminUser admin, AdminPrincipal principal) {
            if (admin == null || !principal.getUsername().equals(admin.getUsername())) {
                return true;
            }

            String role = admin.getAdminLevel();
            if (role == null) {
                return true;
            }

            boolean signedInAsSuperAdmin = principal.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_SUPER_ADMIN".equals(authority.getAuthority()));
            return signedInAsSuperAdmin
                    ? !"SUPER_ADMIN".equalsIgnoreCase(role.trim())
                    : !"ADMIN".equalsIgnoreCase(role.trim());
        }
    }
}

