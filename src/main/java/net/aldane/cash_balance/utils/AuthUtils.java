package net.aldane.cash_balance.utils;

import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.security.model.UserDetails;
import net.aldane.cash_balance_api_server_java.model.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public UserDb getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUser();
        }
        return null;
    }

    public boolean isUserAdmin(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return false;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            var authorities = ((UserDetails) principal).getAuthorities();
            for (var authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return true;
                }
            }
        }
        return false;
    }
}
