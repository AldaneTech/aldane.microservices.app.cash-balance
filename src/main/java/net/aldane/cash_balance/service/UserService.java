package net.aldane.cash_balance.service;



import net.aldane.cash_balance_api_server_java.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getUserById(Long userId);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User createUser(User user);

    boolean deleteUser(Long id);

    User updateUser(User user);
}
