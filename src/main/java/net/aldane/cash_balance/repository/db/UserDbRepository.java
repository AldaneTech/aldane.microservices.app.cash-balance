package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDbRepository extends JpaRepository<UserDb, Long> {
    UserDb findByUsername(String username);

    UserDb findByEmail(String email);
}

