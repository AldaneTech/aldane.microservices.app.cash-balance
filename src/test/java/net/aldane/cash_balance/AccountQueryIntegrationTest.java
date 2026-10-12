package net.aldane.cash_balance;

import net.aldane.cash_balance.mapper.AccountEntryMapper;
import net.aldane.cash_balance.repository.db.entity.*;
import net.aldane.cash_balance.service.DashboardService;
import net.aldane.cash_balance.service.DashboardServiceImpl;
import net.aldane.cash_balance.service.AccountEntryService;
import net.aldane.cash_balance.service.AccountEntryServiceImpl;
import net.aldane.cash_balance.utils.AuthUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest(properties = {"spring.liquibase.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"}, showSql = false)
@Import({DashboardServiceImpl.class, AccountEntryServiceImpl.class})
class AccountQueryIntegrationTest {
    @Autowired TestEntityManager em;
    @Autowired DashboardService queries;
    @Autowired AccountEntryService entryService;
    @MockitoBean AuthUtils auth;
    @MockitoBean AccountEntryMapper mapper;
    WalletDb wallet, other, archived;
    CategoryDb category;
    LocalDate day = LocalDate.of(2026, 10, 12);

    @BeforeEach void seed() {
        em.getEntityManager().createNativeQuery("alter table status alter column id restart with 1").executeUpdate();
        var active = new StatusDb(); active.setName("Active"); em.persist(active);
        assertEquals(1L, active.getId());
        var inactive = new StatusDb(); inactive.setName("Inactive"); em.persist(inactive);
        var role = new RoleDb(); role.setName("USER"); role.setStatus(active); em.persist(role);
        var owner = user("owner", active, role); var stranger = user("stranger", active, role);
        when(auth.getUser()).thenReturn(owner);
        wallet = wallet("mine", owner, active, "70.00");
        other = wallet("other", stranger, active, "999.00");
        archived = wallet("archived", owner, inactive, "123.00");
        category = new CategoryDb(); category.setName("Food"); category.setUser(owner); category.setStatus(active); em.persist(category);
        entry(wallet, "100.00", day, "salary", null);
        entry(wallet, "-30.00", day, "100% food", category);
        entry(wallet, "-5.25", day.minusMonths(1), "old", category);
        entry(other, "999.00", day, "foreign", category);
        entry(archived, "123.00", day, "archived", category);
        em.flush();
        when(mapper.accountEntryDbListToAccountEntryList(anyList())).thenAnswer(i -> ((List<AccountEntryDb>)i.getArgument(0)).stream().map(e -> new net.aldane.cash_balance_api_server_java.model.AccountEntry().id(e.getId()).amount(e.getAmount())).toList());
    }
    private UserDb user(String name, StatusDb status, RoleDb role) {
        var u = new UserDb(); u.setUsername(name); u.setEmail(name+"@example.com"); u.setPassword("hash"); u.setStatus(status); u.setRole(role); return em.persist(u);
    }
    private WalletDb wallet(String name, UserDb owner, StatusDb status, String balance) {
        var w = new WalletDb(); w.setName(name); w.setUser(owner); w.setStatus(status); w.setBudget(new BigDecimal(balance)); return em.persist(w);
    }
    private void entry(WalletDb w, String amount, LocalDate date, String comment, CategoryDb c) {
        var e = new AccountEntryDb(); e.setWallet(w); e.setAmount(new BigDecimal(amount)); e.setDate(date.atTime(23,59,59)); e.setComment(comment); e.setCategory(c); em.persist(e);
    }
    @Test void summaryUsesInclusiveDatesAndExcludesOtherOwnersAndArchivedWallets() {
        var result = queries.getDashboardSummary(null, day, day);
        assertEquals(new BigDecimal("100.00"), result.getIncome());
        assertEquals(new BigDecimal("30.00"), result.getExpenses());
        assertEquals(new BigDecimal("70.00"), result.getNet());
        assertEquals(new BigDecimal("70.00"), result.getBalance());
        assertEquals(2L, result.getEntryCount());
    }
    @Test void emptyPeriodHasZeroTotalsAndCurrentBalance() {
        var result = queries.getDashboardSummary(wallet.getId(), day.plusDays(1), null);
        assertEquals(new BigDecimal("0.00"), result.getNet()); assertEquals(0L, result.getEntryCount());
        assertEquals(new BigDecimal("70.00"), result.getBalance());
    }
    @Test void categoriesIncludeUncategorizedAndMonthsAreOrdered() {
        var categories = queries.getDashboardByCategory(null, day, day);
        assertEquals(2, categories.size());
        assertEquals(new BigDecimal("30.00"), categories.stream().filter(c -> category.getId().equals(c.getCategoryId())).findFirst().orElseThrow().getExpenses());
        assertTrue(categories.stream().anyMatch(c -> c.getCategoryId() == null));
        var months = queries.getDashboardMonthly(null, null, null);
        assertEquals(List.of("2026-09", "2026-10"), months.stream().map(m -> m.getMonth()).toList());
    }
    @Test void paginationFiltersAndLiteralSearchAreAppliedInDatabase() {
        var page = entryService.searchAccountEntries(null, null, null, null, null, null, 0, 1, "amount", "asc");
        assertEquals(3L, page.getTotalElements()); assertEquals(3, page.getTotalPages());
        assertEquals(new BigDecimal("-30.00"), page.getItems().get(0).getAmount());
        var found = entryService.searchAccountEntries(wallet.getId(), day, day, category.getId(), null, "%", 0, 20, "date", "desc");
        assertEquals(1L, found.getTotalElements());
        assertEquals(0L, entryService.searchAccountEntries(null, null, null, null, 999L, null, 0, 20, "id", "asc").getTotalElements());
    }
    @Test void explicitForeignOrArchivedWalletFailsEvenForAdmin() {
        when(auth.isUserAdmin()).thenReturn(true);
        for (var w : List.of(other, archived)) assertEquals(404, assertThrows(ResponseStatusException.class, () -> queries.getDashboardSummary(w.getId(), null, null)).getStatusCode().value());
        assertThrows(ResponseStatusException.class, () -> entryService.searchAccountEntries(null, day, day.minusDays(1), null, null, null, 0, 20, "date", "desc"));
    }
}
