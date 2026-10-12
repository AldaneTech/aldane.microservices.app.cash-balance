package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.AccountEntryDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountEntryDbRepository extends JpaRepository<AccountEntryDb, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<AccountEntryDb> {
    List<AccountEntryDb> findByWallet_User_IdAndWallet_Status_IdOrderByIdAsc(Long userId, Long statusId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from AccountEntryDb e where e.id = :id")
    java.util.Optional<AccountEntryDb> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
    List<AccountEntryDb> findByWallet_IdOrderByDateDesc(Long walletId);

}
