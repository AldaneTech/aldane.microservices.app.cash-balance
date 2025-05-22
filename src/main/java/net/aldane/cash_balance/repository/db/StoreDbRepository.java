package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.StoreDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreDbRepository extends JpaRepository<StoreDb, Long> {
}
