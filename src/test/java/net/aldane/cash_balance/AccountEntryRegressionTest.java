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
    StatusDbRepository statuses = mock(StatusDbRepository.class);
    AccountEntryServiceImpl service = new AccountEntryServiceImpl(entries, wallets, mapper, auth, categories,
            mock(BrandDbRepository.class), mock(StoreDbRepository.class), mock(AccountEntryTypeDbRepository.class), statuses);
    WalletDb wallet; AccountEntryDb entry; StatusDb active;

    private static BigDecimal money(String value) { return new BigDecimal(value).setScale(2, java.math.RoundingMode.UNNECESSARY); }

    @BeforeEach void setup() {
        var user = new UserDb(); user.setId(1L); when(auth.getUser()).thenReturn(user);
        active = new StatusDb(); active.setId(1L);
        wallet = new WalletDb(); wallet.setId(10L); wallet.setUser(user); wallet.setStatus(active); wallet.setBudget(money("100"));
        entry = new AccountEntryDb(); entry.setId(20L); entry.setWallet(wallet); entry.setAmount(money("25"));
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
        verify(entries, never()).delete(any()); verify(wallets, never()).save(any());
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
        var input = new AccountEntry(); input.setId(20L); input.setAmount(money("-10"));
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
        verify(entries, never()).delete(any());
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
}
