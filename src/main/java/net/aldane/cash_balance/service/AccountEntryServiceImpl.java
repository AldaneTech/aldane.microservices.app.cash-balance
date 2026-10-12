package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.AccountEntryMapper;
import net.aldane.cash_balance.repository.db.*;
import net.aldane.cash_balance.repository.db.entity.*;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.AccountEntry;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import net.aldane.cash_balance_api_server_java.model.AccountEntryPage;

@Service
@Transactional(readOnly = true)
public class AccountEntryServiceImpl implements AccountEntryService {
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;
    private final AccountEntryDbRepository entries;
    private final WalletDbRepository wallets;
    private final AccountEntryMapper mapper;
    private final AuthUtils auth;
    private final CategoryDbRepository categories;
    private final BrandDbRepository brands;
    private final StoreDbRepository stores;
    private final AccountEntryTypeDbRepository types;
    private final StatusDbRepository statuses;

    public AccountEntryServiceImpl(AccountEntryDbRepository entries, WalletDbRepository wallets,
            AccountEntryMapper mapper, AuthUtils auth, CategoryDbRepository categories,
            BrandDbRepository brands, StoreDbRepository stores, AccountEntryTypeDbRepository types,
            StatusDbRepository statuses) {
        this.entries = entries; this.wallets = wallets; this.mapper = mapper; this.auth = auth;
        this.categories = categories; this.brands = brands; this.stores = stores;
        this.types = types; this.statuses = statuses;
    }

    @Override
    public AccountEntryPage searchAccountEntries(Long walletId, LocalDate from, LocalDate to, Long categoryId, Long typeId,
            String search, Integer page, Integer size, String sortBy, String direction) {
        checkSearchFilters(walletId, from, to);
        int p = page == null ? 0 : page, s = size == null ? 20 : size;
        String sort = sortBy == null ? "date" : sortBy, dir = direction == null ? "desc" : direction;
        if (p < 0 || p > 21474836 || s < 1 || s > 100 || !List.of("date", "amount", "id").contains(sort)
                || !List.of("asc", "desc").contains(dir) || (categoryId != null && categoryId < 1)
                || (typeId != null && typeId < 1) || (search != null && search.length() > 200)) throw badRequest();
        Long owner = auth.getUser().getId();
        Specification<AccountEntryDb> spec = (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            predicates.add(cb.equal(root.get("wallet").get("user").get("id"), owner));
            predicates.add(cb.equal(root.get("wallet").get("status").get("id"), 1L));
            if (walletId != null) predicates.add(cb.equal(root.get("wallet").get("id"), walletId));
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("date"), from.atStartOfDay()));
            if (to != null) predicates.add(cb.lessThan(root.get("date"), to.plusDays(1).atStartOfDay()));
            if (categoryId != null) predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            if (typeId != null) predicates.add(cb.equal(root.get("accountEntryType").get("id"), typeId));
            if (search != null && !search.isBlank()) {
                String literal = search.toLowerCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
                predicates.add(cb.like(cb.lower(root.get("comment")), "%" + literal + "%", '\\'));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        Sort order = Sort.by(Sort.Direction.fromString(dir), sort);
        if (!sort.equals("id")) order = order.and(Sort.by(Sort.Direction.fromString(dir), "id"));
        var result = entries.findAll(spec, PageRequest.of(p, s, order));
        return new AccountEntryPage().items(mapper.accountEntryDbListToAccountEntryList(result.getContent()))
                .page(p).size(s).totalElements(result.getTotalElements()).totalPages(result.getTotalPages());
    }

    private void checkSearchFilters(Long walletId, LocalDate from, LocalDate to) {
        if ((from != null && to != null && from.isAfter(to)) || LocalDate.MAX.equals(to)
                || (walletId != null && walletId < 1)) throw badRequest();
        if (walletId != null) {
            var wallet = wallets.findById(walletId).orElseThrow(AccountEntryServiceImpl::notFound);
            if (wallet.getUser() == null || !wallet.getUser().getId().equals(auth.getUser().getId())
                    || wallet.getStatus() == null || !Long.valueOf(1L).equals(wallet.getStatus().getId())) throw notFound();
        }
    }

    @Override
    public List<AccountEntry> getAccountEntries(List<String> statesIds) {
        return mapper.accountEntryDbListToAccountEntryList(auth.isUserAdmin()
                ? entries.findAll(Sort.by("id"))
                : entries.findByWallet_User_IdAndWallet_Status_IdOrderByIdAsc(auth.getUser().getId(), 1L));
    }

    @Override
    public AccountEntry getAccountEntryById(Long id) {
        var entry = entries.findById(id).orElseThrow(AccountEntryServiceImpl::notFound);
        checkWallet(entry.getWallet());
        return mapper.accountEntryDbToAccountEntry(entry);
    }

    @Override
    public List<AccountEntry> getAccountEntryByWalletId(Long id) {
        checkWallet(wallets.findById(id).orElseThrow(AccountEntryServiceImpl::notFound));
        return mapper.accountEntryDbListToAccountEntryList(entries.findByWallet_IdOrderByDateDesc(id));
    }

    @Override
    @Transactional
    public AccountEntry createAccountEntry(AccountEntry input) {
        validateAmount(input.getAmount());
        if (input.getId() != null || input.getWallet() == null || input.getWallet().getId() == null) {
            throw badRequest();
        }
        var wallet = lockedWallet(input.getWallet().getId());
        var entry = mapper.accountEntryToAccountEntryDb(input);
        entry.setId(null);
        entry.setWallet(wallet);
        entry.setStatus(statuses.findById(1L).orElseThrow(AccountEntryServiceImpl::badRequest));
        if (entry.getDate() == null) entry.setDate(LocalDateTime.now(java.time.ZoneOffset.UTC));
        resolveReferences(entry, wallet.getUser().getId());
        entry.setAmount(normalizeAmount(input.getAmount(), entry.getAccountEntryType()));
        changeBalance(wallet, entry.getAmount());
        return mapper.accountEntryDbToAccountEntry(entries.save(entry));
    }

    @Override
    @Transactional
    public boolean deleteAccountEntry(Long id) {
        var entry = entries.findByIdForUpdate(id).orElseThrow(AccountEntryServiceImpl::notFound);
        var wallet = lockedWallet(entry.getWallet().getId());
        changeBalance(wallet, entry.getAmount().negate());
        entries.delete(entry);
        return true;
    }

    @Override
    @Transactional
    public AccountEntry updateAccountEntry(AccountEntry input) {
        validateAmount(input.getAmount());
        if (input.getId() == null) throw badRequest();
        var entry = entries.findByIdForUpdate(input.getId()).orElseThrow(AccountEntryServiceImpl::notFound);
        var wallet = lockedWallet(entry.getWallet().getId());
        // The wallet is immutable for an existing entry.
        if (input.getWallet() != null && !wallet.getId().equals(input.getWallet().getId())) throw badRequest();
        // Resolve and validate a detached candidate before modifying managed entities or the balance.
        var candidate = mapper.accountEntryToAccountEntryDb(input);
        if (input.getTypeOperation() == null) candidate.setAccountEntryType(entry.getAccountEntryType());
        resolveReferences(candidate, wallet.getUser().getId());
        BigDecimal amount = normalizeAmount(input.getAmount(), candidate.getAccountEntryType());
        changeBalance(wallet, amount.subtract(entry.getAmount()));
        entry.setAmount(amount);
        entry.setComment(input.getComment());
        entry.setCategory(candidate.getCategory());
        entry.setBrand(candidate.getBrand());
        entry.setStore(candidate.getStore());
        entry.setAccountEntryType(candidate.getAccountEntryType());
        if (input.getDate() != null) entry.setDate(candidate.getDate());
        return mapper.accountEntryDbToAccountEntry(entries.save(entry));
    }

    private WalletDb lockedWallet(Long id) {
        var wallet = wallets.findByIdForUpdate(id).orElseThrow(AccountEntryServiceImpl::notFound);
        // An eager association may already have loaded this wallet before the lock was acquired.
        entityManager.refresh(wallet);
        checkWallet(wallet);
        return wallet;
    }

    private void checkWallet(WalletDb wallet) {
        if (wallet == null || wallet.getUser() == null || wallet.getStatus() == null
                || !Long.valueOf(1L).equals(wallet.getStatus().getId())
                || (!auth.isUserAdmin() && !wallet.getUser().getId().equals(auth.getUser().getId()))) {
            throw notFound();
        }
    }

    private void resolveReferences(AccountEntryDb entry, Long ownerId) {
        if (entry.getCategory() != null) {
            var ref = categories.findById(requiredId(entry.getCategory().getId())).orElseThrow(AccountEntryServiceImpl::badRequest);
            checkReference(ref.getUser(), ref.getStatus(), ownerId); entry.setCategory(ref);
        }
        if (entry.getBrand() != null) {
            var ref = brands.findById(requiredId(entry.getBrand().getId())).orElseThrow(AccountEntryServiceImpl::badRequest);
            checkReference(ref.getUser(), ref.getStatus(), ownerId); entry.setBrand(ref);
        } else entry.setBrand(null);
        if (entry.getStore() != null) {
            var ref = stores.findById(requiredId(entry.getStore().getId())).orElseThrow(AccountEntryServiceImpl::badRequest);
            checkReference(ref.getUser(), ref.getStatus(), ownerId); entry.setStore(ref);
        } else entry.setStore(null);
        if (entry.getAccountEntryType() == null) throw badRequest();
        var type = types.findById(requiredId(entry.getAccountEntryType().getId())).orElseThrow(AccountEntryServiceImpl::badRequest);
        if (type.getStatus() == null || !Long.valueOf(1L).equals(type.getStatus().getId())) throw badRequest();
        entry.setAccountEntryType(type);
    }

    private static BigDecimal normalizeAmount(BigDecimal amount, AccountEntryTypeDb type) {
        if (Long.valueOf(1L).equals(type.getId())) return amount.abs().setScale(2, RoundingMode.UNNECESSARY);
        if (Long.valueOf(2L).equals(type.getId())) return amount.abs().negate().setScale(2, RoundingMode.UNNECESSARY);
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    private static Long requiredId(Long id) { if (id == null) throw badRequest(); return id; }

    private static void checkReference(UserDb user, StatusDb status, Long ownerId) {
        if (user == null || !ownerId.equals(user.getId()) || status == null || !Long.valueOf(1L).equals(status.getId())) throw badRequest();
    }

    private static final BigDecimal MAX_MONEY = new BigDecimal("9999999999999999.99");

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.abs().compareTo(MAX_MONEY) > 0) throw badRequest();
        try {
            amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw badRequest();
        }
    }

    private void changeBalance(WalletDb wallet, BigDecimal delta) {
        validateAmount(wallet.getBudget());
        BigDecimal balance = wallet.getBudget().add(delta);
        validateAmount(balance);
        wallet.setBudget(balance.setScale(2, RoundingMode.UNNECESSARY));
        wallet.setLastModification(LocalDateTime.now(java.time.ZoneOffset.UTC));
        wallets.save(wallet);
    }

    private static ResponseStatusException notFound() { return new ResponseStatusException(HttpStatus.NOT_FOUND); }
    private static ResponseStatusException badRequest() { return new ResponseStatusException(HttpStatus.BAD_REQUEST); }
}
