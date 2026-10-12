package net.aldane.cash_balance;

import net.aldane.cash_balance.mapper.AccountEntryMapper;
import net.aldane.cash_balance.repository.db.*;
import net.aldane.cash_balance.repository.db.entity.*;
import net.aldane.cash_balance.service.AccountEntryServiceImpl;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.AccountEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountEntryRegressionTest {
    AccountEntryDbRepository entries = mock(AccountEntryDbRepository.class);
    WalletDbRepository wallets = mock(WalletDbRepository.class);
    AccountEntryMapper mapper = mock(AccountEntryMapper.class);
    AuthUtils auth = mock(AuthUtils.class);
    CategoryDbRepository categories = mock(CategoryDbRepository.class);
    AccountEntryTypeDbRepository types = mock(AccountEntryTypeDbRepository.class);
    BrandDbRepository brands = mock(BrandDbRepository.class);
    StoreDbRepository stores = mock(StoreDbRepository.class);
    StatusDbRepository statuses = mock(StatusDbRepository.class);
    AccountEntryServiceImpl service = new AccountEntryServiceImpl(entries, wallets, mapper, auth, categories,
            brands, stores, types, statuses);
    WalletDb wallet; AccountEntryDb entry; StatusDb active;

    private static BigDecimal money(String value) { return new BigDecimal(value).setScale(2, java.math.RoundingMode.UNNECESSARY); }

    @BeforeEach void setup() {
        var user = new UserDb(); user.setId(1L); when(auth.getUser()).thenReturn(user);
        active = new StatusDb(); active.setId(1L);
        wallet = new WalletDb(); wallet.setId(10L); wallet.setUser(user); wallet.setStatus(active); wallet.setBudget(money("100"));
        entry = new AccountEntryDb(); entry.setId(20L); entry.setWallet(wallet); entry.setAmount(money("25"));
        for (long id : List.of(1L, 2L, 3L)) {
            var type = new AccountEntryTypeDb(); type.setId(id); type.setStatus(active);
            when(types.findById(id)).thenReturn(Optional.of(type));
        }
        entry.setAccountEntryType(types.findById(1L).orElseThrow());
        when(mapper.accountEntryToAccountEntryDb(any())).thenAnswer(invocation -> {
            AccountEntry input = invocation.getArgument(0);
            var result = new AccountEntryDb(); result.setAmount(input.getAmount()); result.setComment(input.getComment());
            if (input.getDate() != null) result.setDate(input.getDate().atStartOfDay());
            if (input.getTypeOperation() != null) { var type = new AccountEntryTypeDb(); type.setId(input.getTypeOperation().getId()); result.setAccountEntryType(type); }
            if (input.getCategory() != null) { var ref = new CategoryDb(); ref.setId(input.getCategory().getId()); result.setCategory(ref); }
            if (input.getBrand() != null) { var ref = new BrandDb(); ref.setId(input.getBrand().getId()); result.setBrand(ref); }
            if (input.getStore() != null) { var ref = new StoreDb(); ref.setId(input.getStore().getId()); result.setStore(ref); }
            return result;
        });
        when(entries.findById(20L)).thenReturn(Optional.of(entry));
        when(entries.findByIdForUpdate(20L)).thenReturn(Optional.of(entry));
        when(wallets.findByIdForUpdate(10L)).thenReturn(Optional.of(wallet));
        when(statuses.findById(1L)).thenReturn(Optional.of(active));
        when(entries.save(any())).thenAnswer(i -> i.getArgument(0));
        ReflectionTestUtils.setField(service, "entityManager", mock(jakarta.persistence.EntityManager.class));
    }

    @Test void cannotReadAnotherUsersEntry() {
        var other = new UserDb(); other.setId(2L); wallet.setUser(other);
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.getAccountEntryById(20L)).getStatusCode().value());
        verifyNoInteractions(mapper);
    }
    @Test void cannotDeleteAnotherUsersEntry() {
        var other = new UserDb(); other.setId(2L); wallet.setUser(other);
        assertThrows(ResponseStatusException.class, () -> service.deleteAccountEntry(20L));
        verify(entries, never()).delete(any(AccountEntryDb.class)); verify(wallets, never()).save(any());
    }
    @Test void cannotUpdateAnotherUsersEntry() {
        var other = new UserDb(); other.setId(2L); wallet.setUser(other);
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("50"));
        assertThrows(ResponseStatusException.class, () -> service.updateAccountEntry(input));
        assertEquals(money("100"), wallet.getBudget()); assertEquals(money("25"), entry.getAmount());
    }
    @Test void listIsScopedToCurrentUser() {
        when(entries.findByWallet_User_IdAndWallet_Status_IdOrderByIdAsc(1L, 1L)).thenReturn(List.of());
        service.getAccountEntries(List.of());
        verify(entries).findByWallet_User_IdAndWallet_Status_IdOrderByIdAsc(1L, 1L);
        verify(entries, never()).findAll(any(org.springframework.data.domain.Sort.class));
    }
    @Test void deleteReversesBalanceWithoutMutatingAmount() {
        assertTrue(service.deleteAccountEntry(20L));
        assertEquals(money("75"), wallet.getBudget()); assertEquals(money("25"), entry.getAmount()); verify(entries).delete(entry);
    }
    @Test void updateAppliesOnlyAmountDifference() {
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("10"));
        input.setTypeOperation(new net.aldane.cash_balance_api_server_java.model.AccountEntryType().id(2L));
        service.updateAccountEntry(input);
        assertEquals(money("65"), wallet.getBudget()); assertEquals(money("-10"), entry.getAmount());
    }
    @Test void rejectsInvalidAmountsBeforeWriting() {
        for (BigDecimal amount : Arrays.asList(null, new BigDecimal("0.001"), money("10000000000000000"), money("-10000000000000000"))) {
            var input = new AccountEntry(); input.setAmount(amount);
            assertThrows(ResponseStatusException.class, () -> service.createAccountEntry(input));
        }
        verifyNoInteractions(entries, wallets);
    }
    @Test void creationCannotOverwriteExistingEntry() {
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("10"));
        assertThrows(ResponseStatusException.class, () -> service.createAccountEntry(input));
        verify(entries, never()).save(any());
    }
    @Test void rejectsDeletedWalletEvenForAdmin() {
        when(auth.isUserAdmin()).thenReturn(true); var deleted = new StatusDb(); deleted.setId(2L); wallet.setStatus(deleted);
        assertThrows(ResponseStatusException.class, () -> service.deleteAccountEntry(20L));
        verify(entries, never()).delete(any(AccountEntryDb.class));
    }
    @Test void cannotCreateInAnotherUsersWallet() {
        var other = new UserDb(); other.setId(2L); wallet.setUser(other);
        var input = new AccountEntry(); input.setAmount(money("10"));
        var dtoWallet = new net.aldane.cash_balance_api_server_java.model.Wallet(); dtoWallet.setId(10L); input.setWallet(dtoWallet);
        assertThrows(ResponseStatusException.class, () -> service.createAccountEntry(input));
        verifyNoInteractions(mapper); verify(entries, never()).save(any());
    }
    @Test void rejectsCategoryOwnedByAnotherUser() {
        var input = new AccountEntry(); input.setAmount(money("10"));
        var dtoWallet = new net.aldane.cash_balance_api_server_java.model.Wallet(); dtoWallet.setId(10L); input.setWallet(dtoWallet);
        var mapped = new AccountEntryDb(); mapped.setAmount(money("10")); var category = new CategoryDb(); category.setId(30L); mapped.setCategory(category);
        when(mapper.accountEntryToAccountEntryDb(input)).thenReturn(mapped);
        var other = new UserDb(); other.setId(2L); category.setUser(other); category.setStatus(active);
        when(categories.findById(30L)).thenReturn(Optional.of(category));
        assertThrows(ResponseStatusException.class, () -> service.createAccountEntry(input));
        assertEquals(money("100"), wallet.getBudget()); verify(entries, never()).save(any());
    }
    @Test void databaseFailurePropagatesForTransactionRollback() {
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("30"));
        when(entries.save(any())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("constraint"));
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> service.updateAccountEntry(input));
    }

    @Test void decimalArithmeticIsExactAcrossUpdatesAndDeletion() {
        wallet.setBudget(money("0.10")); entry.setAmount(money("0.10"));
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("0.30"));
        service.updateAccountEntry(input);
        assertEquals(money("0.30"), wallet.getBudget());
        input.setAmount(money("0.20")); service.updateAccountEntry(input);
        assertEquals(money("0.20"), wallet.getBudget());
        service.deleteAccountEntry(20L);
        assertEquals(money("0.00"), wallet.getBudget());
    }

    @Test void decimalArithmeticPreservesCentsForLargeBalances() {
        wallet.setBudget(money("9999999999999999.98")); entry.setAmount(money("0.01"));
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("0.02"));
        service.updateAccountEntry(input);
        assertEquals(money("9999999999999999.99"), wallet.getBudget());
    }

    @Test void balanceOverflowIsRejectedBeforePersisting() {
        wallet.setBudget(money("9999999999999999.99")); entry.setAmount(money("0.01"));
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("0.02"));
        assertThrows(ResponseStatusException.class, () -> service.updateAccountEntry(input));
        assertEquals(money("9999999999999999.99"), wallet.getBudget());
        verify(wallets, never()).save(any()); verify(entries, never()).save(any());
    }

    @Test void extraTrailingZerosAreAcceptedWithoutRounding() {
        var input = new AccountEntry(); input.setId(20L); input.setAmount(new BigDecimal("25.0000"));
        service.updateAccountEntry(input);
        assertEquals(money("100.00"), wallet.getBudget());
    }

    private AccountEntry input(BigDecimal amount, Long typeId) {
        var input = new AccountEntry().amount(amount).wallet(new net.aldane.cash_balance_api_server_java.model.Wallet().id(10L));
        if (typeId != null) input.setTypeOperation(new net.aldane.cash_balance_api_server_java.model.AccountEntryType().id(typeId));
        return input;
    }

    @Test void creationNormalizesBothInputSignsUsingOperationType() {
        for (long type : List.of(1L, 2L)) {
            for (String raw : List.of("10", "-10")) {
                wallet.setBudget(money("100"));
                service.createAccountEntry(input(money(raw), type));
                var captor = org.mockito.ArgumentCaptor.forClass(AccountEntryDb.class);
                verify(entries, atLeastOnce()).save(captor.capture());
                var saved = captor.getValue();
                assertEquals(money(type == 1 ? "10" : "-10"), saved.getAmount());
                assertEquals(money(type == 1 ? "110" : "90"), wallet.getBudget());
            }
        }
    }

    @Test void editingExpenseIntoIncomeAppliesFullSignedDifference() {
        entry.setAmount(money("-25")); entry.setAccountEntryType(types.findById(2L).orElseThrow());
        var input = input(money("-25"), 1L); input.setId(20L);
        service.updateAccountEntry(input);
        assertEquals(money("25"), entry.getAmount()); assertEquals(money("150"), wallet.getBudget());
    }

    @Test void editsAllFieldsAndCanClearOptionalReferences() {
        var category = new CategoryDb(); category.setId(30L); category.setUser(wallet.getUser()); category.setStatus(active);
        var brand = new BrandDb(); brand.setId(40L); brand.setUser(wallet.getUser()); brand.setStatus(active);
        var store = new StoreDb(); store.setId(50L); store.setUser(wallet.getUser()); store.setStatus(active);
        when(categories.findById(30L)).thenReturn(Optional.of(category));
        when(brands.findById(40L)).thenReturn(Optional.of(brand));
        when(stores.findById(50L)).thenReturn(Optional.of(store));
        var input = input(money("25"), 2L).id(20L).comment("Updated").date(java.time.LocalDate.of(2026,10,10))
                .category(new net.aldane.cash_balance_api_server_java.model.Category().id(30L))
                .brand(new net.aldane.cash_balance_api_server_java.model.Brand().id(40L))
                .store(new net.aldane.cash_balance_api_server_java.model.Store().id(50L));
        service.updateAccountEntry(input);
        assertSame(category, entry.getCategory()); assertSame(brand, entry.getBrand()); assertSame(store, entry.getStore());
        assertEquals("Updated", entry.getComment()); assertEquals(input.getDate().atStartOfDay(), entry.getDate());
        assertEquals(2L, entry.getAccountEntryType().getId()); assertEquals(money("50"), wallet.getBudget());
        var cleared = new AccountEntry().id(20L).amount(money("25")).comment("");
        service.updateAccountEntry(cleared);
        assertNull(entry.getCategory()); assertNull(entry.getBrand()); assertNull(entry.getStore());
        assertEquals("", entry.getComment()); assertEquals(input.getDate().atStartOfDay(), entry.getDate());
        assertEquals(2L, entry.getAccountEntryType().getId()); assertEquals(money("50"), wallet.getBudget());
    }

    @Test void invalidEditedReferenceDoesNotChangeEntryOrBalance() {
        var other = new UserDb(); other.setId(2L);
        var foreign = new BrandDb(); foreign.setId(40L); foreign.setUser(other); foreign.setStatus(active);
        when(brands.findById(40L)).thenReturn(Optional.of(foreign));
        var input = input(money("99"), 2L).id(20L).brand(new net.aldane.cash_balance_api_server_java.model.Brand().id(40L));
        assertThrows(ResponseStatusException.class, () -> service.updateAccountEntry(input));
        assertEquals(money("100"), wallet.getBudget()); assertEquals(money("25"), entry.getAmount());
        assertEquals(1L, entry.getAccountEntryType().getId()); verify(wallets, never()).save(any());
    }

    @Test void creationRequiresActiveOperationType() {
        assertThrows(ResponseStatusException.class, () -> service.createAccountEntry(input(money("10"), null)));
        assertThrows(ResponseStatusException.class, () -> service.createAccountEntry(input(money("10"), 999L)));
        var inactive = new StatusDb(); inactive.setId(2L); types.findById(2L).orElseThrow().setStatus(inactive);
        assertThrows(ResponseStatusException.class, () -> service.createAccountEntry(input(money("10"), 2L)));
        assertEquals(money("100"), wallet.getBudget()); verify(entries, never()).save(any());
    }

    @Test void otherActiveTypesPreserveSignedAmount() {
        service.createAccountEntry(input(money("-10"), 3L));
        assertEquals(money("90"), wallet.getBudget());
    }

    @Test void editingCannotMoveEntryToAnotherWallet() {
        var input = input(money("10"), 1L).id(20L).wallet(new net.aldane.cash_balance_api_server_java.model.Wallet().id(999L));
        assertThrows(ResponseStatusException.class, () -> service.updateAccountEntry(input));
        assertEquals(money("100"), wallet.getBudget()); verify(entries, never()).save(any());
    }
}
