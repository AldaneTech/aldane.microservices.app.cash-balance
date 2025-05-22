package net.aldane.cash_balance.mapper;

import net.aldane.cash_balance.repository.db.entity.RoleDb;
import net.aldane.cash_balance_api_server_java.model.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    Role roleDbToRole(RoleDb roleDb);

    List<Role> roleDbListToRoleList(List<RoleDb> roleDb);

    RoleDb roleToRoleDb(Role role);
}

