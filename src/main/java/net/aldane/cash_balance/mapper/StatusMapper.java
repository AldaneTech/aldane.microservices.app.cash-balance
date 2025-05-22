package net.aldane.cash_balance.mapper;

import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance_api_server_java.model.Status;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface StatusMapper {
    Status statusDbToStatus(StatusDb statusDb);

    List<Status> statusDbListToStatusList(List<StatusDb> statusDb);

    StatusDb statusToStatusDb(Status status);
}
