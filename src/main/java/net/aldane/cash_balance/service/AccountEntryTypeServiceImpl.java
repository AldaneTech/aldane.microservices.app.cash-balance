package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.AccountEntryTypeMapper;
import net.aldane.cash_balance.repository.db.AccountEntryTypeDbRepository;
import net.aldane.cash_balance.repository.db.entity.AccountEntryTypeDb;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance_api_server_java.model.AccountEntryType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountEntryTypeServiceImpl implements AccountEntryTypeService {

    private final AccountEntryTypeDbRepository accountEntryTypeDbRepository;
    private final AccountEntryTypeMapper accountEntryTypeMapper;
    @Autowired
    private StatusUtils statusUtils;
    @Autowired
    private AuthUtils authUtils;

    private final Logger log = LogManager.getLogger(this.getClass());

    public AccountEntryTypeServiceImpl(AccountEntryTypeDbRepository accountEntryTypeDbRepository, AccountEntryTypeMapper accountEntryTypeMapper) {
        this.accountEntryTypeDbRepository = accountEntryTypeDbRepository;
        this.accountEntryTypeMapper = accountEntryTypeMapper;
    }

    @Override
    public List<AccountEntryType> getAccountEntryTypes() {
        try {
            List<AccountEntryTypeDb> accountEntryTypesList;
            if(authUtils.userIsAdmin()){
                accountEntryTypesList = accountEntryTypeDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

            }else {
                accountEntryTypesList = accountEntryTypeDbRepository.findAllByStatus_OrderByIdAsc(statusUtils.getActiveStatus());
            }
            return accountEntryTypeMapper.accountEntryTypeDbListToAccountEntryTypeList(accountEntryTypesList);
        } catch (Exception e) {
            log.error("Error obtaining account entry types");
            return new ArrayList<>();
        }
    }

    @Override
    public AccountEntryType getAccountEntryTypeById(Long accountEntryTypeId) {
        try {
            var accountEntryType = accountEntryTypeDbRepository.findById(accountEntryTypeId).orElse(null);
            if(accountEntryType.getStatus().equals(statusUtils.getActiveStatus())){
                return accountEntryTypeMapper.accountEntryTypeDbToAccountEntryType(accountEntryType);
            } else {
                if(authUtils.userIsAdmin()){
                    return accountEntryTypeMapper.accountEntryTypeDbToAccountEntryType(accountEntryType);
                }
            }

        } catch (Exception e) {
            log.error("Error obtaining account entry type with id: {}", accountEntryTypeId);

        }
        return null;
    }

    @Override
    public AccountEntryType createAccountEntryType(AccountEntryType accountEntryType) {
        try {
            if(accountEntryType.getName() == null || accountEntryType.getName().trim().isBlank()){
                log.info("AccountEntryType name can't be empty");
                return null;
            }
            AccountEntryTypeDb newAccountEntryType = new AccountEntryTypeDb();
            newAccountEntryType.setName(accountEntryType.getName());
            if (accountEntryType.getComment().trim().isBlank()) {
                newAccountEntryType.setComment(String.format("Created by userId %d", authUtils.getUser().getId()));
            } else {
                newAccountEntryType.setComment(accountEntryType.getComment());
            }
            newAccountEntryType.setStatus(statusUtils.getActiveStatus());
            newAccountEntryType.setLastModification(OffsetDateTime.now().toLocalDateTime());
            var accountEntryTypeSaved = accountEntryTypeDbRepository.save(newAccountEntryType);
            return accountEntryTypeMapper.accountEntryTypeDbToAccountEntryType(accountEntryTypeSaved);
        } catch (Exception e) {
            log.error("Error creating account entry type");
            return null;
        }
    }

    @Override
    public boolean deleteAccountEntryType(Long id) {
        try {
            var accountEntryType = accountEntryTypeDbRepository.findById(id).orElse(null);
            if(accountEntryType != null){
                accountEntryType.setStatus(statusUtils.getDeletedStatus());
                accountEntryType.setComment(String.format("Deleted by userId %d", authUtils.getUser().getId()));
                accountEntryType.setLastModification(OffsetDateTime.now().toLocalDateTime());
                accountEntryTypeDbRepository.save(accountEntryType);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting account entry type with id: {}", id);

        }
        return false;
    }

    @Override
    public AccountEntryType updateAccountEntryType(AccountEntryType accountEntryType) {
        try {
            var accountEntryTypeDb = accountEntryTypeDbRepository.findById(accountEntryType.getId()).orElse(null);
            if(accountEntryTypeDb != null){
                if(accountEntryType.getName() != null && !accountEntryType.getName().trim().isBlank()){
                    accountEntryTypeDb.setName(accountEntryType.getName());
                }
                if(accountEntryType.getComment() == null || accountEntryType.getComment().trim().isBlank()){
                    accountEntryTypeDb.setComment(String.format("Updated by userId %d", authUtils.getUser().getId()));
                } else {
                    accountEntryTypeDb.setComment(accountEntryType.getComment());
                }
                if(accountEntryType.getStatus() != null && accountEntryType.getStatus().getId() != null){
                    accountEntryTypeDb.setStatus(statusUtils.findStatusDb(accountEntryType.getStatus().getId()));
                }
                accountEntryTypeDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                return accountEntryTypeMapper.accountEntryTypeDbToAccountEntryType(accountEntryTypeDbRepository.save(accountEntryTypeDb));
            }
        } catch (Exception e) {
            log.error("Error updating account entry type with id: {}", accountEntryType.getId());

        }
        return null;
    }
}
