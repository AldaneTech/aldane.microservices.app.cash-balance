package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.RoleMapper;
import net.aldane.cash_balance.repository.db.RoleDbRepository;
import net.aldane.cash_balance_api_server_java.model.Role;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class RoleServiceImpl implements RoleService {
    private final RoleDbRepository roleDbRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleDbRepository roleDbRepository, RoleMapper roleMapper) {
        this.roleDbRepository = roleDbRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<Role> getRoles(List<String> statesIds) {
        try {
            var rolesList = roleDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return roleMapper.roleDbListToRoleList(rolesList);
        } catch (Exception e) {
            System.out.println("Error obtaining roles");
            return new ArrayList<>();
        }
    }

    @Override
    public Role getRoleById(Long roleId) {
        try {
            var role = roleDbRepository.findById(roleId).orElse(null);
            return roleMapper.roleDbToRole(role);
        } catch (Exception e) {
            System.out.println("Error obtaining role with id: " + roleId);
            return null;
        }
    }

    @Override
    public Role createRole(Role role) {
        try {
            var roleSaved = roleDbRepository.save(roleMapper.roleToRoleDb(role));
            return roleMapper.roleDbToRole(roleSaved);
        } catch (Exception e) {
            System.out.println("Error creating role");
            return null;
        }
    }

    @Override
    public boolean deleteRole(Long id) {
        try {
            roleDbRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting role with id: " + id);
            return false;
        }
    }

    @Override
    public Role updateRole(Role role) {
        try {
            var roleDb = roleDbRepository.findById(role.getId()).orElse(null);
            roleDb.setName(role.getName());
            //roleDb.setComment(role.getComment());
            return roleMapper.roleDbToRole(roleDbRepository.save(roleDb));
        } catch (Exception e) {
            System.out.println("Error updating role with id: " + role.getId());
            return null;
        }
    }
}
