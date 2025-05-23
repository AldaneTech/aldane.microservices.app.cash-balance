package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.AccountEntryType;

import java.util.List;

public interface AccountEntryTypeService {
    List<AccountEntryType> getAccountEntryTypes();

    AccountEntryType getAccountEntryTypeById(Long accountEntryTypeId);

    AccountEntryType createAccountEntryType(AccountEntryType accountEntryType);

    boolean deleteAccountEntryType(Long id);

    AccountEntryType updateAccountEntryType(AccountEntryType accountEntryType);
}
