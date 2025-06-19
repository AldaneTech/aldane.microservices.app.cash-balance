package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.AccountEntry;

import java.util.List;

public interface AccountEntryService {

    List<AccountEntry> getAccountEntries();

    AccountEntry getAccountEntryById(Long accountEntryId);

    AccountEntry createAccountEntry(AccountEntry accountEntry);

    boolean deleteAccountEntry(Long id);

    AccountEntry updateAccountEntry(AccountEntry accountEntry);

    List<AccountEntry> getAccountEntriesByWalletId(Long walletId);
}
