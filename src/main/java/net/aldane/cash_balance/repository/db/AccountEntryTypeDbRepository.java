package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.AccountEntryTypeDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountEntryTypeDbRepository extends JpaRepository<AccountEntryTypeDb, Long> {
}
