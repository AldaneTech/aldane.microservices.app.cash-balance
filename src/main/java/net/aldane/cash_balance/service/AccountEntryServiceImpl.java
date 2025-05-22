package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.AccountEntryMapper;
import net.aldane.cash_balance.repository.db.AccountEntryDbRepository;
import net.aldane.cash_balance.repository.db.WalletDbRepository;
import net.aldane.cash_balance.repository.db.entity.AccountEntryDb;
import net.aldane.cash_balance.service.exception.AccountEntryServiceException;
import net.aldane.cash_balance_api_server_java.model.AccountEntry;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountEntryServiceImpl implements AccountEntryService {

    private final AccountEntryDbRepository accountEntryDbRepository;
    private final WalletDbRepository walletDbRepository;
    private final AccountEntryMapper accountEntryMapper;

    public AccountEntryServiceImpl(AccountEntryDbRepository accountEntryDbRepository, WalletDbRepository walletDbRepository, AccountEntryMapper accountEntryMapper) {
        this.accountEntryDbRepository = accountEntryDbRepository;
        this.walletDbRepository = walletDbRepository;
        this.accountEntryMapper = accountEntryMapper;
    }

    @Override
    public List<AccountEntry> getAccountEntries(List<String> statesIds) {
        try {
            var accountEntrysList = accountEntryDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return accountEntryMapper.accountEntryDbListToAccountEntryList(accountEntrysList);
        } catch (Exception e) {
            System.out.println("Error obtaining account entries");
            return new ArrayList<>();
        }
    }

    @Override
    public AccountEntry getAccountEntryById(Long accountEntryId) {
        try {
            var accountEntry = accountEntryDbRepository.findById(accountEntryId).orElse(null);
            return accountEntryMapper.accountEntryDbToAccountEntry(accountEntry);
        } catch (Exception e) {
            System.out.println("Error obtaining account entry with id: " + accountEntryId);
            return null;
        }
    }

    @Override
    public List<AccountEntry> getAccountEntryByWalletId(Long walletId) {
        try {
            var accountEntry = accountEntryDbRepository.findByWallet_IdOrderByDateDesc(walletId);
            return accountEntryMapper.accountEntryDbListToAccountEntryList(accountEntry);
        } catch (Exception e) {
            System.out.println("Error obtaining account entry with id: " + walletId);
            return null;
        }
    }

    @Override
    public AccountEntry createAccountEntry(AccountEntry accountEntry) {
        try {
            var accountEntryDb = accountEntryMapper.accountEntryToAccountEntryDb(accountEntry);
            if (accountEntry.getBrand() == null || accountEntry.getBrand().getId() == null) {
                accountEntryDb.setBrand(null);
            }
            if (accountEntry.getStore() == null || accountEntry.getStore().getId() == null) {
                accountEntryDb.setStore(null);
            }
            var accountEntrySaved = accountEntryDbRepository.save(accountEntryDb);
            updateBalanceWallet(accountEntrySaved);
            return accountEntryMapper.accountEntryDbToAccountEntry(accountEntrySaved);
        } catch (Exception e) {
            System.out.println("Error creating account entry");
            return null;
        }
    }

    @Override
    public boolean deleteAccountEntry(Long id) {
        try {
            var accountEntry = accountEntryDbRepository.findById(id).orElse(null);
            if (accountEntry != null) {
                accountEntry.setAmount(accountEntry.getAmount() * -1);
                updateBalanceWallet(accountEntry);
                accountEntryDbRepository.deleteById(id);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error deleting account entry with id: " + id);
        }
        return false;
    }

    @Override
    public AccountEntry updateAccountEntry(AccountEntry accountEntry) {
        try {
            var accountEntryDb = accountEntryDbRepository.findById(accountEntry.getId()).orElse(null);
            if (accountEntryDb != null) {
                accountEntryDb.setAmount(accountEntryDb.getAmount() * -1);
                updateBalanceWallet(accountEntryDb);
                accountEntryDb.setComment(accountEntry.getComment());
                accountEntryDb.setAmount(accountEntry.getAmount());
                var accountEntrySaved = accountEntryDbRepository.save(accountEntryDb);
                updateBalanceWallet(accountEntrySaved);
                return accountEntryMapper.accountEntryDbToAccountEntry(accountEntrySaved);
            }
        } catch (Exception e) {
            System.out.println("Error updating account entry with id: " + accountEntry.getId());
        }
        return null;
    }

    private void updateBalanceWallet(AccountEntryDb accountEntry) {
        var wallet = walletDbRepository.findById(accountEntry.getWallet().getId()).orElse(null);
        if (wallet != null) {
            wallet.setBudget(wallet.getBudget() + accountEntry.getAmount());
            walletDbRepository.save(wallet);
        } else {
            throw new AccountEntryServiceException("There is no wallet for the acount entry with id: " + accountEntry.getId());
        }
    }
}
