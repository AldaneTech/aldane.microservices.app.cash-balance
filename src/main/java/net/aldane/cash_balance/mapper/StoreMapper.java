package net.aldane.cash_balance.mapper;

import net.aldane.cash_balance.repository.db.entity.StoreDb;
import net.aldane.cash_balance_api_server_java.model.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper
public interface StoreMapper {
    Store storeDbToStore(StoreDb brandDb);

    List<Store> storeDbListToStoreList(List<StoreDb> brandDb);

    @Mapping(target = "lastModification", ignore = true)
    StoreDb storeToStoreDb(Store brand);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    @Named("offsetDateTimeToLocalDateTime")
    default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
}
