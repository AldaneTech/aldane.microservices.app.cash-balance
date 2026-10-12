package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.AccountEntry;
import net.aldane.cash_balance_api_server_java.model.AccountEntryPage;
import java.time.LocalDate;

import java.util.List;

public interface AccountEntryService {

    AccountEntryPage searchAccountEntries(Long walletId, LocalDate from, LocalDate to, Long categoryId,
            Long typeId, String search, Integer page, Integer size, String sortBy, String direction);

    List<AccountEntry> getAccountEntries(List<String> accountEntrysIds);

    AccountEntry getAccountEntryById(Long accountEntryId);

    AccountEntry createAccountEntry(AccountEntry accountEntry);

    boolean deleteAccountEntry(Long id);

    AccountEntry updateAccountEntry(AccountEntry accountEntry);

    List<AccountEntry> getAccountEntryByWalletId(Long walletId);
}
