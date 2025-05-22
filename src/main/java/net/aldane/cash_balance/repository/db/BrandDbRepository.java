package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.BrandDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandDbRepository extends JpaRepository<BrandDb, Long> {
    List<BrandDb> findByUserIdOrderByNameAsc(Long userId);
}
