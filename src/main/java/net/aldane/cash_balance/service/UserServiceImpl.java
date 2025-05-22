package net.aldane.cash_balance.service;


import net.aldane.cash_balance.mapper.UserMapper;
import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance_api_server_java.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    private final UserDbRepository userDbRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDbRepository userDbRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userDbRepository = userDbRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public List<User> getUsers(List<String> userIds) {
        try {
            var usersList = userDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return userMapper.userDbListToUserList(usersList);
        } catch (Exception e) {
            System.out.println("Error obtaining users");
            return new ArrayList<>();
        }
    }

    @Override
    public User getUserById(Long userId) {
        try {
            var user = userDbRepository.findById(userId).orElse(null);
            return userMapper.userDbToUser(user);
        } catch (Exception e) {
            System.out.println("Error obtaining user with id: " + userId);
            return null;
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            var user = userDbRepository.findByUsername(username);
            return userMapper.userDbToUser(user);
        } catch (Exception e) {
            System.out.println("Error obtaining user with username: " + username);
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            var user = userDbRepository.findByEmail(email);
            return userMapper.userDbToUser(user);
        } catch (Exception e) {
            System.out.println("Error obtaining user with email: " + email);
            return null;
        }
    }

    @Override
    public User createUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            //user.setLastAccess(OffsetDateTime.now());
            user.setRecordDate(OffsetDateTime.now());
            var userSaved = userDbRepository.save(userMapper.userToUserDb(user));
            return userMapper.userDbToUser(userSaved);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("username")) {
                //throw new UsernameServiceException("El nombre de usuario ya está en uso.", ex);
            } else if (ex.getMessage().contains("email")) {
                //throw new UserServiceException("El email ya está en uso.", ex);
            }
        } catch (Exception ex) {
            System.out.println("Error creating user");
        }
        return null;
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            userDbRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting user with id: " + id);
            return false;
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            //user.setLastAccess(OffsetDateTime.now());
            var userDb = userDbRepository.findById(user.getId()).orElse(null);
            if (userDb != null) {
                if (user.getPassword() != null) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                return userMapper.userDbToUser(userDbRepository.save(userMapper.userToUserDb(user)));
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error updating user with id: " + user.getId());
            return null;
        }
    }
}
