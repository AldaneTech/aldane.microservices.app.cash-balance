package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDbRepository extends JpaRepository<UserDb, Long> {
    UserDb findByUsername(String username);

    UserDb findByEmail(String email);

    List<UserDb> findAllByStatusAndUserOrderByNameAsc(StatusDb status, UserDb user);
}

