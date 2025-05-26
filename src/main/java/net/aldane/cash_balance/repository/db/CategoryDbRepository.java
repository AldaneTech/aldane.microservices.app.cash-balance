package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.CategoryDb;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.StoreDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryDbRepository extends JpaRepository<CategoryDb, Long> {
    List<CategoryDb> findByUserIdOrderByNameAsc(Long userId);
    List<CategoryDb> findAllByStatusAndUserOrderByNameAsc(StatusDb status, UserDb user);
    Optional<CategoryDb> findByIdAndStatusAndUser(Long id, StatusDb status, UserDb user);
}
