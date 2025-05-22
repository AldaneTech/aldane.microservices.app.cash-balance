package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.RoleDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDbRepository extends JpaRepository<RoleDb, Long> {
    RoleDb findByName(String name);
}
