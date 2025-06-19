package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.AccountEntryDb;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.repository.db.entity.WalletDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountEntryDbRepository extends JpaRepository<AccountEntryDb, Long> {
    List<AccountEntryDb> findByWallet_IdOrderByDateDesc(Long walletId);

    List<AccountEntryDb> findByUserIdOrderByNameAsc(Long userId);

    List<AccountEntryDb> findAllByStatusAndWalletOrderByNameAsc(StatusDb status, WalletDb wallet);

    Optional<AccountEntryDb> findByIdAndStatusAndUser(Long id, StatusDb status, UserDb user);

    @Query(value = """
            SELECT * FROM account_entry 
            WHERE wallet_id IN (
                SELECT id FROM wallet 
                WHERE user_id = :userId AND status_id = :statusId
            )
            AND status_id = :statusId
            ORDER BY date DESC
            """, nativeQuery = true)
    List<AccountEntryDb> findByUserIdAndActiveWallets(@Param("userId") Long userId, @Param("statusId") Long statusId);

}
