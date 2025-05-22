package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.AccountEntryTypeMapper;
import net.aldane.cash_balance.repository.db.AccountEntryTypeDbRepository;
import net.aldane.cash_balance_api_server_java.model.AccountEntryType;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AccountEntryTypeServiceImpl implements AccountEntryTypeService {

    private final AccountEntryTypeDbRepository accountEntryTypeDbRepository;
    private final AccountEntryTypeMapper accountEntryTypeMapper;

    public AccountEntryTypeServiceImpl(AccountEntryTypeDbRepository accountEntryTypeDbRepository, AccountEntryTypeMapper accountEntryTypeMapper) {
        this.accountEntryTypeDbRepository = accountEntryTypeDbRepository;
        this.accountEntryTypeMapper = accountEntryTypeMapper;
    }

    @Override
    public List<AccountEntryType> getAccountEntryTypes(List<String> statesIds) {
        try {
            var accountEntryTypesList = accountEntryTypeDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return accountEntryTypeMapper.accountEntryTypeDbListToAccountEntryTypeList(accountEntryTypesList);
        } catch (Exception e) {
            System.out.println("Error obtaining account entry types");
            return new ArrayList<>();
        }
    }

    @Override
    public AccountEntryType getAccountEntryTypeById(Long accountEntryTypeId) {
        try {
            var accountEntryType = accountEntryTypeDbRepository.findById(accountEntryTypeId).orElse(null);
            return accountEntryTypeMapper.accountEntryTypeDbToAccountEntryType(accountEntryType);
        } catch (Exception e) {
            System.out.println("Error obtaining account entry type with id: " + accountEntryTypeId);
            return null;
        }
    }

    @Override
    public AccountEntryType createAccountEntryType(AccountEntryType accountEntryType) {
        try {
            accountEntryType.setLastModification(OffsetDateTime.now());
            var accountEntryTypeSaved = accountEntryTypeDbRepository.save(accountEntryTypeMapper.accountEntryTypeToAccountEntryTypeDb(accountEntryType));
            return accountEntryTypeMapper.accountEntryTypeDbToAccountEntryType(accountEntryTypeSaved);
        } catch (Exception e) {
            System.out.println("Error creating account entry type");
            return null;
        }
    }

    @Override
    public boolean deleteAccountEntryType(Long id) {
        try {
            accountEntryTypeDbRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting account entry type with id: " + id);
            return false;
        }
    }

    @Override
    public AccountEntryType updateAccountEntryType(AccountEntryType accountEntryType) {
        try {
            accountEntryType.setLastModification(OffsetDateTime.now());
            var accountEntryTypeDb = accountEntryTypeDbRepository.findById(accountEntryType.getId()).orElse(null);
            accountEntryTypeDb.setName(accountEntryType.getName());
            accountEntryTypeDb.setComment(accountEntryType.getComment());
            return accountEntryTypeMapper.accountEntryTypeDbToAccountEntryType(accountEntryTypeDbRepository.save(accountEntryTypeDb));
        } catch (Exception e) {
            System.out.println("Error updating account entry type with id: " + accountEntryType.getId());
            return null;
        }
    }
}
