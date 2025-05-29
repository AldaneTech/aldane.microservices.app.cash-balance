package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.repository.db.entity.WalletDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletDbRepository extends JpaRepository<WalletDb, Long> {
    List<WalletDb> findByUserId(Long userId);

    List<WalletDb> findByUserIdOrderByNameAsc(Long userId);

    List<WalletDb> findAllByStatusAndUserOrderByNameAsc(StatusDb status, UserDb user);

    Optional<WalletDb> findByIdAndStatusAndUser(Long id, StatusDb status, UserDb user);

}
