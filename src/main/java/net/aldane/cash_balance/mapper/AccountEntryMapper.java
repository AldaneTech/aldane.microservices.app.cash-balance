package net.aldane.cash_balance.mapper;

import net.aldane.cash_balance.repository.db.entity.AccountEntryDb;
import net.aldane.cash_balance_api_server_java.model.AccountEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UserMapper.class, BrandMapper.class, StoreMapper.class, CategoryMapper.class, WalletMapper.class, AccountEntryTypeMapper.class})
public interface AccountEntryMapper {
    @Mapping(source = "accountEntryType", target = "typeOperation")
    AccountEntry accountEntryDbToAccountEntry(AccountEntryDb accountEntry);

    List<AccountEntry> accountEntryDbListToAccountEntryList(List<AccountEntryDb> accountEntry);

    @Mapping(source = "typeOperation", target = "accountEntryType")
    AccountEntryDb accountEntryToAccountEntryDb(AccountEntry accountEntry);
}
