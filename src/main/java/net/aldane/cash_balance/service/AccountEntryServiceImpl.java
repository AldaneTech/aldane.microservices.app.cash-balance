package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.AccountEntryMapper;
import net.aldane.cash_balance.repository.db.AccountEntryDbRepository;
import net.aldane.cash_balance.repository.db.WalletDbRepository;
import net.aldane.cash_balance.repository.db.entity.AccountEntryDb;
import net.aldane.cash_balance.service.exception.AccountEntryServiceException;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance.utils.WalletUtils;
import net.aldane.cash_balance_api_server_java.model.AccountEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountEntryServiceImpl implements AccountEntryService {

    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private StatusUtils statusUtils;
    @Autowired
    private WalletUtils walletUtils;
    private final AccountEntryDbRepository accountEntryDbRepository;
    private final WalletDbRepository walletDbRepository;
    private final AccountEntryMapper accountEntryMapper;

    private final WalletService walletService;
    private final Logger log = LogManager.getLogger(this.getClass());

    public AccountEntryServiceImpl(AccountEntryDbRepository accountEntryDbRepository, WalletDbRepository walletDbRepository, AccountEntryMapper accountEntryMapper, WalletService walletService) {
        this.accountEntryDbRepository = accountEntryDbRepository;
        this.walletDbRepository = walletDbRepository;
        this.accountEntryMapper = accountEntryMapper;
        this.walletService = walletService;
    }

    @Override
    public List<AccountEntry> getAccountEntries() {
        try {
            List<AccountEntryDb> accountEntryDbList;
            if (authUtils.isUserAdmin()) {
                accountEntryDbList = accountEntryDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            } else {
                accountEntryDbList = accountEntryDbRepository.findByUserIdAndActiveWallets(authUtils.getUser().getId(), statusUtils.getActiveStatus().getId());
            }
            return accountEntryMapper.accountEntryDbListToAccountEntryList(accountEntryDbList);
        } catch (Exception e) {
            log.error("Error obtaining Account Entries");
            return new ArrayList<>();
        }
    }

    @Override
    public AccountEntry getAccountEntryById(Long accountEntryId) {
        try {
            var accountEntry = accountEntryDbRepository.findById(accountEntryId).orElse(null);
            if (accountEntry != null) {
                if (authUtils.isUserAdmin()) {
                    return accountEntryMapper.accountEntryDbToAccountEntry(accountEntry);
                } else {
                    var wallet = walletService.getWalletById(accountEntry.getWallet().getId());
                    if (accountEntry.getStatus().equals(statusUtils.getActiveStatus()) && wallet != null && wallet.getStatus().getName().equals(statusUtils.getActiveStatus().getName())) {
                        return accountEntryMapper.accountEntryDbToAccountEntry(accountEntry);
                    } else {
                        log.warn("Account Entry with id {} is not active or does not belong to the user", accountEntryId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining account entry with id: {}", accountEntryId);
        }
        return null;
    }

    @Override
    public List<AccountEntry> getAccountEntriesByWalletId(Long walletId) {
        try {
            List<AccountEntryDb> accountEntryDbList = new ArrayList<>();
            if (authUtils.isUserAdmin()) {
                accountEntryDbList = accountEntryDbRepository.findByWallet_IdOrderByDateDesc(walletId);
            } else {
                if (walletService.getWalletById(walletId) != null) {
                    accountEntryDbList = accountEntryDbRepository.findAllByStatusAndWalletOrderByNameAsc(statusUtils.getActiveStatus(), walletUtils.findWalletDb(walletId));
                }
            }
            return accountEntryMapper.accountEntryDbListToAccountEntryList(accountEntryDbList);
        } catch (Exception e) {
            log.error("Error obtaining Account Entries for wallet with id: {}", walletId);
            return new ArrayList<>();
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
