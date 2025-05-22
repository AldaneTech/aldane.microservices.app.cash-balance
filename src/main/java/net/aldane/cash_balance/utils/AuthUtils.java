package net.aldane.cash_balance.utils;

import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.security.model.UserDetails;
import net.aldane.cash_balance_api_server_java.model.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public UserDb getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUser();
        }
        return null;
    }
}