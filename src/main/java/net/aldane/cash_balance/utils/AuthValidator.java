package net.aldane.cash_balance.utils;

import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.security.model.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authValidator")
public class AuthValidator {

    public boolean isOwnerOrAdmin(Long resourceOwnerId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails customUser) {
            Long currentUserId = customUser.getUser().getId();
            boolean isAdmin = customUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            return isAdmin || currentUserId.equals(resourceOwnerId);
        }

        return false;
    }
}
