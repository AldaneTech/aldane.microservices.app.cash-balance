package net.aldane.cash_balance.controller;

import jakarta.validation.Valid;
import net.aldane.cash_balance.service.AccountEntryTypeService;
import net.aldane.cash_balance_api_server_java.api.AccountEntryTypeApi;
import net.aldane.cash_balance_api_server_java.model.AccountEntryType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountEntryTypeController implements AccountEntryTypeApi {
    private final AccountEntryTypeService accountEntryTypeService;

    public AccountEntryTypeController(AccountEntryTypeService accountEntryTypeService) {
        this.accountEntryTypeService = accountEntryTypeService;
    }

    @Override
    public ResponseEntity<AccountEntryType> createAccountEntryType(@Valid AccountEntryType accountEntryType) {
        var newAccountEntryType = accountEntryTypeService.createAccountEntryType(accountEntryType);
        return newAccountEntryType != null ? ResponseEntity.ok(newAccountEntryType) : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteAccountEntryType(Long accountEntryTypeId) {
        var accountEntryType = accountEntryTypeService.deleteAccountEntryType(accountEntryTypeId);
        return accountEntryType ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<AccountEntryType> getAccountEntryTypeById(Long accountEntryTypeId) {
        var accountEntryType = accountEntryTypeService.getAccountEntryTypeById(accountEntryTypeId);
        return accountEntryType != null ? ResponseEntity.ok(accountEntryType) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<AccountEntryType>> getAccountEntryTypes() {
        var states = accountEntryTypeService.getAccountEntryTypes(new ArrayList<>());
        return ResponseEntity.ok(states);
    }

    @Override
    public ResponseEntity<AccountEntryType> updateAccountEntryType(@Valid AccountEntryType accountEntryType) {
        return accountEntryTypeService.updateAccountEntryType(accountEntryType) != null ? ResponseEntity.ok(accountEntryType) : ResponseEntity.badRequest().build();
    }
}
