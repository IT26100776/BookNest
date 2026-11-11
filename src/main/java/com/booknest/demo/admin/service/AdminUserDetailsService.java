package com.booknest.demo.admin.service;

import com.booknest.demo.admin.model.AdminUser;
import java.util.Locale;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminManager adminManager;

    public AdminUserDetailsService(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        //Search the account using the username
        AdminUser admin = adminManager.findByUsername(username);
        if (admin == null || admin.getAdminLevel() == null) {
            throw new UsernameNotFoundException("Admin account unavailable");
        }

        //Give the super admin access to regular admin routes
        String[] roles = switch (admin.getAdminLevel().trim().toUpperCase(Locale.ROOT)) {
            case "ADMIN" -> new String[] {"ROLE_ADMIN"};
            case "SUPER_ADMIN" -> new String[] {"ROLE_ADMIN", "ROLE_SUPER_ADMIN"};
            default -> throw new UsernameNotFoundException("Admin account unavailable");
        };

        //Spring security uses the stored hash to check the password
        return new AdminPrincipal(admin, roles);
    }

    public static final class AdminPrincipal extends User {

        private final String userId;

        private AdminPrincipal(AdminUser admin, String[] roles) {
            super(admin.getUsername(), admin.getPassword(), AuthorityUtils.createAuthorityList(roles));
            this.userId = admin.getUserId();
        }

        public String getUserId() {
            return userId;
        }
    }
}
