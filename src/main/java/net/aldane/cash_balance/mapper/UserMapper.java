package net.aldane.cash_balance.mapper;

import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance_api_server_java.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper
public interface UserMapper {
    @Mapping(source = "recordDate", target = "recordDate", qualifiedByName = "localDateTimeToOffsetDateTimeUser")
    User userDbToUser(UserDb userDb);

    List<User> userDbListToUserList(List<UserDb> userDb);

    @Mapping(source = "recordDate", target = "recordDate", qualifiedByName = "offsetDateTimeToLocalDateTimeUser")
    @Mapping(target = "lastAccess", ignore = true)
    UserDb userToUserDb(User user);

    @Named("localDateTimeToOffsetDateTimeUser")
    default OffsetDateTime localDateTimeToOffsetDateTimeUser(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    @Named("offsetDateTimeToLocalDateTimeUser")
    default LocalDateTime offsetDateTimeToLocalDateTimeUser(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
}

