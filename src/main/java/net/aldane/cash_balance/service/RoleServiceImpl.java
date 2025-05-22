package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.RoleMapper;
import net.aldane.cash_balance.repository.db.RoleDbRepository;
import net.aldane.cash_balance.repository.db.entity.RoleDb;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance_api_server_java.model.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private StatusUtils statusUtils;
    @Autowired
    private AuthUtils authUtils;
    private final Logger log = LogManager.getLogger(this.getClass());
    public RoleServiceImpl(RoleDbRepository roleDbRepository, RoleMapper roleMapper) {
        this.roleDbRepository = roleDbRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<Role> getRoles() {
        try {
            var rolesList = roleDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return roleMapper.roleDbListToRoleList(rolesList);
        } catch (Exception e) {
            log.error("Error when querying roles in database");
            return new ArrayList<>();
        }
    }

    @Override
    public Role getRoleById(Long roleId) {
        try {
            var role = roleDbRepository.findById(roleId).orElse(null);
            return roleMapper.roleDbToRole(role);
        } catch (Exception e) {
            log.error("Error obtaining role with id: {}", roleId);
            return null;
        }
    }

    @Override
    public Role createRole(Role role) {
        try {
            if(role.getName() == null || role.getName().trim().isBlank()){
                log.info("Role name can't be empty");
                return null;
            }
            RoleDb newRole = new RoleDb();
            newRole.setName(role.getName());
            newRole.setStatus(statusUtils.getActiveStatus());
            var roleSaved = roleDbRepository.save(newRole);
            return roleMapper.roleDbToRole(roleSaved);
        } catch (Exception e) {
            log.error("Role name already exists");
            return null;
        }
    }

    @Override
    public boolean deleteRole(Long id) {
        try {
            var roleDb = roleDbRepository.findById(id).orElse(null);
            if(roleDb != null){
                roleDb.setStatus(statusUtils.getDeletedStatus());
                roleDb.setComment(String.format("Deleted by userId %d", authUtils.getUser().getId()));
                roleDbRepository.save(roleDb);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting role with id: {}", id);

        }
        return false;
    }

    @Override
    public Role updateRole(Role role) {
        try {
            var roleDb = roleDbRepository.findById(role.getId()).orElse(null);
            if(roleDb != null){
                if(role.getName() != null && !role.getName().trim().isBlank()){
                    roleDb.setName(role.getName());
                }
                if(role.getComment() == null || role.getComment().trim().isBlank()){
                    roleDb.setComment(String.format("Updated by userId %d", authUtils.getUser().getId()));
                } else {
                    roleDb.setComment(role.getComment());
                }
                if(role.getStatus().getId() != null){
                    roleDb.setStatus(statusUtils.findStatusDb(role.getStatus().getId()));
                }
                return roleMapper.roleDbToRole(roleDbRepository.save(roleDb));
            }
        } catch (Exception e) {
            log.error("Error updating role with id: {}", role.getId());
        }
        return null;
    }
}
