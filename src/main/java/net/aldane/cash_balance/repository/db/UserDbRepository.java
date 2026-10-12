package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDbRepository extends JpaRepository<UserDb, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from UserDb u where u.id = :id")
    java.util.Optional<UserDb> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);

    UserDb findByUsername(String username);

    UserDb findByEmail(String email);
}

