package net.aldane.cash_balance.repository.db;

import net.aldane.cash_balance.repository.db.entity.CategoryDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDbRepository extends JpaRepository<CategoryDb, Long> {
}
