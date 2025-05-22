package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.WalletDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletDbRepository extends JpaRepository<WalletDb, Long> {
    List<WalletDb> findByUserId(Long userId);
}
