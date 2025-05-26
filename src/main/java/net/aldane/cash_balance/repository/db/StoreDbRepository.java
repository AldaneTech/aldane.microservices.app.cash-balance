package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.BrandDb;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.StoreDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreDbRepository extends JpaRepository<StoreDb, Long> {

    List<StoreDb> findByUserIdOrderByNameAsc(Long userId);
    List<StoreDb> findAllByStatusAndUserOrderByNameAsc(StatusDb status, UserDb user);
    Optional<StoreDb> findByIdAndStatusAndUser(Long id, StatusDb status, UserDb user);

}
