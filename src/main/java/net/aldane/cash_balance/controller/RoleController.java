package net.aldane.cash_balance.controller;

import jakarta.validation.Valid;
import net.aldane.cash_balance.service.RoleService;
import net.aldane.cash_balance_api_server_java.api.RoleApi;
import net.aldane.cash_balance_api_server_java.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RoleController implements RoleApi {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public ResponseEntity<Role> createRole(@Valid Role role) {
        var newRole = roleService.createRole(role);
        return newRole != null ? ResponseEntity.ok(newRole) : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteRole(Long roleId) {
        var role = roleService.deleteRole(roleId);
        return role ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Role> getRoleById(Long roleId) {
        var role = roleService.getRoleById(roleId);
        return role != null ? ResponseEntity.ok(role) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<Role>> getRoles() {
        var states = roleService.getRoles();
        return ResponseEntity.ok(states);
    }

    @Override
    public ResponseEntity<Role> updateRole(@Valid Role role) {
        var result = roleService.updateRole(role);
        return  result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
    }
}
