package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.AccountEntryDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountEntryDbRepository extends JpaRepository<AccountEntryDb, Long> {
    List<AccountEntryDb> findByWallet_IdOrderByDateDesc(Long walletId);

}
