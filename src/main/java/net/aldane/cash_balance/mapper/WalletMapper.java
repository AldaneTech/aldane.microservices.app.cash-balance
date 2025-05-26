package net.aldane.cash_balance.mapper;

import net.aldane.cash_balance.repository.db.entity.WalletDb;
import net.aldane.cash_balance_api_server_java.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(uses = {UserMapper.class})
public interface WalletMapper {
    @Mapping(source = "lastModification", target = "lastModification", qualifiedByName = "localDateTimeToOffsetDateTime")
    Wallet walletDbToWallet(WalletDb walletDb);
    List<Wallet> walletDbListToWalletList(List<WalletDb> walletDb);
    @Mappings({
            @Mapping(source = "lastModification", target = "lastModification", qualifiedByName = "offsetDateTimeToLocalDateTime"),
            @Mapping(target = "user", ignore = true)  // MapStruct usará UserMapper automáticamente
    })    WalletDb walletToWalletDb(Wallet wallet);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    @Named("offsetDateTimeToLocalDateTime")
    default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
}
