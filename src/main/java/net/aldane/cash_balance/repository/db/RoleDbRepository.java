package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.RoleDb;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDbRepository extends JpaRepository<RoleDb, Long> {
    RoleDb findByName(String name);
    List<RoleDb> findAllByStatus_OrderByIdAsc(StatusDb status);
}
