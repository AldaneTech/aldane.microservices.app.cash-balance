package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.AccountEntryTypeDb;
import net.aldane.cash_balance.repository.db.entity.BrandDb;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandDbRepository extends JpaRepository<BrandDb, Long> {
    List<BrandDb> findByUserIdOrderByNameAsc(Long userId);

    List<BrandDb> findAllByStatusAndUserOrderByNameAsc(StatusDb status, UserDb user);
    Optional<BrandDb> findByIdAndStatusAndUser(Long id, StatusDb status, UserDb user);

}
