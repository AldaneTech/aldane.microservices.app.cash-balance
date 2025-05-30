package net.aldane.cash_balance.controller;


import jakarta.validation.Valid;
import net.aldane.cash_balance.service.UserService;
import net.aldane.cash_balance_api_server_java.api.UserApi;
import net.aldane.cash_balance_api_server_java.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController implements UserApi {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<List<User>> getUsers() {
        var users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<User> getUserByEmail(String email) {
        var user = userService.getUserByEmail(email);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<User> getUserById(Long userId) {
        var user = userService.getUserById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<User> getUserByUsername(String username) {
        var user = userService.getUserByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<User> createUser(@Valid User user) {
        var newUser = userService.createUser(user);
        return newUser != null ? ResponseEntity.ok(newUser) : ResponseEntity.badRequest().build();
    }

    @Override
    @PreAuthorize("@authValidator.isOwnerOrAdmin(#userId)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        var user = userService.deleteUser(userId);
        return user ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<User> updateUser(@Valid User user) {
        var result = userService.updateUser(user);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
    }
}
