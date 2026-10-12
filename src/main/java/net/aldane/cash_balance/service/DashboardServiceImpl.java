package net.aldane.cash_balance.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.aldane.cash_balance.repository.db.WalletDbRepository;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    private final WalletDbRepository wallets;
    private final AuthUtils auth;
    @PersistenceContext private EntityManager em;

    public DashboardServiceImpl(WalletDbRepository wallets, AuthUtils auth) {
        this.wallets = wallets; this.auth = auth;
    }

    @Override
    public DashboardSummary getDashboardSummary(Long walletId, LocalDate from, LocalDate to) {
        var rows = aggregate(walletId, from, to, "");
        Object[] row = rows.get(0);
        BigDecimal income = money(row[0]), expenses = money(row[1]);
        String jpql = "select sum(w.budget) from WalletDb w where w.user.id = :owner and w.status.id = 1";
        if (walletId != null) jpql += " and w.id = :wallet";
        var query = em.createQuery(jpql, BigDecimal.class).setParameter("owner", auth.getUser().getId());
        if (walletId != null) query.setParameter("wallet", walletId);
        return new DashboardSummary().income(income).expenses(expenses).net(income.subtract(expenses))
                .entryCount(((Number) row[2]).longValue()).balance(money(query.getSingleResult()));
    }

    @Override
    public List<CategoryTotals> getDashboardByCategory(Long walletId, LocalDate from, LocalDate to) {
        return aggregate(walletId, from, to, "category").stream().map(row -> {
            BigDecimal income = money(row[0]), expenses = money(row[1]);
            return new CategoryTotals().income(income).expenses(expenses).net(income.subtract(expenses))
                    .entryCount(((Number) row[2]).longValue()).categoryId((Long) row[3]).categoryName((String) row[4]);
        }).toList();
    }

    @Override
    public List<MonthlyTotals> getDashboardMonthly(Long walletId, LocalDate from, LocalDate to) {
        return aggregate(walletId, from, to, "month").stream().map(row -> {
            BigDecimal income = money(row[0]), expenses = money(row[1]);
            return new MonthlyTotals().income(income).expenses(expenses).net(income.subtract(expenses))
                    .entryCount(((Number) row[2]).longValue()).month(String.format(Locale.ROOT, "%04d-%02d", ((Number) row[3]).intValue(), ((Number) row[4]).intValue()));
        }).toList();
    }

    private List<Object[]> aggregate(Long walletId, LocalDate from, LocalDate to, String group) {
        checkFilters(walletId, from, to);
        String jpql = "select sum(case when e.amount > 0 then e.amount else 0 end), "
                + "sum(case when e.amount < 0 then -e.amount else 0 end), count(e)";
        if (group.equals("category")) jpql += ", c.id, c.name";
        if (group.equals("month")) jpql += ", year(e.date), month(e.date)";
        jpql += " from AccountEntryDb e";
        if (group.equals("category")) jpql += " left join e.category c";
        jpql += " where e.wallet.user.id = :owner and e.wallet.status.id = 1";
        if (group.equals("month")) jpql += " and e.date is not null";
        if (walletId != null) jpql += " and e.wallet.id = :wallet";
        if (from != null) jpql += " and e.date >= :fromDate";
        if (to != null) jpql += " and e.date < :toDate";
        if (group.equals("category")) jpql += " group by c.id, c.name order by c.id";
        if (group.equals("month")) jpql += " group by year(e.date), month(e.date) order by year(e.date), month(e.date)";
        var query = em.createQuery(jpql, Object[].class).setParameter("owner", auth.getUser().getId());
        if (walletId != null) query.setParameter("wallet", walletId);
        if (from != null) query.setParameter("fromDate", from.atStartOfDay());
        if (to != null) query.setParameter("toDate", to.plusDays(1).atStartOfDay());
        return query.getResultList();
    }

    private void checkFilters(Long walletId, LocalDate from, LocalDate to) {
        if ((from != null && to != null && from.isAfter(to)) || LocalDate.MAX.equals(to)
                || (walletId != null && walletId < 1)) throw badRequest();
        if (walletId != null) {
            var wallet = wallets.findById(walletId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if (!wallet.getUser().getId().equals(auth.getUser().getId()) || wallet.getStatus() == null || !Long.valueOf(1).equals(wallet.getStatus().getId()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
    private static BigDecimal money(Object value) { return value == null ? BigDecimal.ZERO.setScale(2) : ((BigDecimal) value).setScale(2); }
    private static ResponseStatusException badRequest() { return new ResponseStatusException(HttpStatus.BAD_REQUEST); }
}
