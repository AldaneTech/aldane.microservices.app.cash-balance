package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.StatusDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDbRepository extends JpaRepository<StatusDb, Long> {
}
