package net.aldane.cash_balance.utils;

import net.aldane.cash_balance.repository.db.RoleDbRepository;
import net.aldane.cash_balance.repository.db.entity.RoleDb;
import org.springframework.stereotype.Component;

@Component
public class RoleUtils {

    private final RoleDbRepository roleDbRepository;

    public RoleUtils(RoleDbRepository roleDbRepository) {
        this.roleDbRepository = roleDbRepository;
    }

    public RoleDb getUserRole() {
        return roleDbRepository.findById(2L).orElse(null);
    }
}
