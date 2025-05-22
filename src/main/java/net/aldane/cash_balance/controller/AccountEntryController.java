package net.aldane.cash_balance.controller;

import jakarta.validation.Valid;
import net.aldane.cash_balance.service.AccountEntryService;
import net.aldane.cash_balance_api_server_java.api.AccountEntryApi;
import net.aldane.cash_balance_api_server_java.model.AccountEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountEntryController implements AccountEntryApi {
    private final AccountEntryService accountEntryService;

    public AccountEntryController(AccountEntryService accountEntryService) {
        this.accountEntryService = accountEntryService;
    }

    @Override
    public ResponseEntity<AccountEntry> createAccountEntry(@Valid AccountEntry accountEntry) {
        var newAccountEntry = accountEntryService.createAccountEntry(accountEntry);
        return newAccountEntry != null ? ResponseEntity.ok(newAccountEntry) : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteAccountEntry(Long accountEntryId) {
        var accountEntry = accountEntryService.deleteAccountEntry(accountEntryId);
        return accountEntry ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<AccountEntry> getAccountEntryById(Long accountEntryId) {
        var accountEntry = accountEntryService.getAccountEntryById(accountEntryId);
        return accountEntry != null ? ResponseEntity.ok(accountEntry) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<AccountEntry>> getAccountEntryByWalletId(Long walletId) {
        var states = accountEntryService.getAccountEntryByWalletId(walletId);
        return ResponseEntity.ok(states);
    }

    @Override
    public ResponseEntity<List<AccountEntry>> getAccountEntries() {
        var states = accountEntryService.getAccountEntries(new ArrayList<>());
        return ResponseEntity.ok(states);
    }

    @Override
    public ResponseEntity<AccountEntry> updateAccountEntry(@Valid AccountEntry accountEntry) {
        return accountEntryService.updateAccountEntry(accountEntry) != null ? ResponseEntity.ok(accountEntry) : ResponseEntity.badRequest().build();
    }
}
