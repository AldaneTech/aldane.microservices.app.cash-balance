package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.Role;

import java.util.List;

public interface RoleService {
    List<Role> getRoles();

    Role getRoleById(Long roleId);

    Role createRole(Role role);

    boolean deleteRole(Long id);

    Role updateRole(Role role);
}
