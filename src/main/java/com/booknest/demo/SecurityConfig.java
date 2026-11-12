package com.booknest.demo;

import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
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

    public static final String API_CSRF_TOKEN_ATTRIBUTE = "booknest.apiCsrfToken";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AdminManager adminManager)
            throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .addFilterBefore(new AdminSessionFilter(adminManager), AuthorizationFilter.class)
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
                                "/js/admin-navigation.js")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        .requestMatchers("/api/admin/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**")
                        .permitAll()

                        .requestMatchers("/api/books", "/api/books/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/status")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/orders", "/api/orders/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews", "/api/reviews/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()
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

    private static final class SpaCsrfTokenRequestHandler
            implements CsrfTokenRequestHandler {

        private final CsrfTokenRequestAttributeHandler plain =
                new CsrfTokenRequestAttributeHandler();

        private final XorCsrfTokenRequestAttributeHandler xor =
                new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                Supplier<CsrfToken> csrfToken) {

            this.xor.handle(request, response, csrfToken);

            // API clients send the raw header token, while login forms use the masked token.
            request.setAttribute(API_CSRF_TOKEN_ATTRIBUTE, csrfToken.get());
        }

        @Override
        public String resolveCsrfTokenValue(
                HttpServletRequest request,
                CsrfToken csrfToken) {

            String headerValue = request.getHeader(csrfToken.getHeaderName());

            return StringUtils.hasText(headerValue)
                    ? this.plain.resolveCsrfTokenValue(request, csrfToken)
                    : this.xor.resolveCsrfTokenValue(request, csrfToken);
        }
    }
}

