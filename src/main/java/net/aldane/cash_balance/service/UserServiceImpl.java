package net.aldane.cash_balance.service;


import net.aldane.cash_balance.mapper.UserMapper;
import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.repository.db.entity.StoreDb;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.RoleUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance_api_server_java.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private StatusUtils statusUtils;
    @Autowired
    private RoleUtils roleUtils;
    private final UserDbRepository userDbRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LogManager.getLogger(this.getClass());

    public UserServiceImpl(UserDbRepository userDbRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userDbRepository = userDbRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public User getUserById(Long userId) {
        try {
            var user = userDbRepository.findById(userId).orElse(null);
            if (user != null) {
                if (authUtils.isUserAdmin()) {
                    return userMapper.userDbToUser(user);
                } else {
                    if (user.getStatus().equals(statusUtils.getActiveStatus()) && userId.equals(authUtils.getUser().getId())) {
                        user.setLastAccess(OffsetDateTime.now().toLocalDateTime());
                        try {
                            userDbRepository.save(user);
                        } catch (DataIntegrityViolationException e) {
                            log.error("Error updating last access for user with id: {}", userId, e);
                        }
                        return userMapper.userDbToUser(user);
                    } else {
                        log.warn("User with id {} is not active or does not belong to the user", userId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining user with id: {}", userId);
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        try {
            List<UserDb> userDbList;
            if (authUtils.isUserAdmin()) {
                userDbList = userDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            } else {
                var user = getUserById(authUtils.getUser().getId());
                return user != null ? Collections.singletonList(user) : new ArrayList<>();
            }
            return userMapper.userDbListToUserList(userDbList);
        } catch (Exception e) {
            log.error("Error obtaining users");
            return new ArrayList<>();
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            var user = userDbRepository.findByUsername(username);
            if (user != null) {
                if (authUtils.isUserAdmin()) {
                    return userMapper.userDbToUser(user);
                } else {
                    if (user.getStatus().equals(statusUtils.getActiveStatus()) && username.equals(authUtils.getUser().getUsername())) {
                        user.setLastAccess(OffsetDateTime.now().toLocalDateTime());
                        try {
                            userDbRepository.save(user);
                        } catch (DataIntegrityViolationException e) {
                            log.error("Error updating last access for user with username: {}", username, e);
                        }
                        return userMapper.userDbToUser(user);
                    } else {
                        log.warn("User with name {} is not active or does not belong to the user", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining user with name: {}", username);
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            var user = userDbRepository.findByEmail(email);
            if (user != null) {
                if (authUtils.isUserAdmin()) {
                    return userMapper.userDbToUser(user);
                } else {
                    if (user.getStatus().equals(statusUtils.getActiveStatus()) && email.equals(authUtils.getUser().getEmail())) {
                        user.setLastAccess(OffsetDateTime.now().toLocalDateTime());
                        try {
                            userDbRepository.save(user);
                        } catch (DataIntegrityViolationException e) {
                            log.error("Error updating last access for user with email: {}", email, e);
                        }
                        return userMapper.userDbToUser(user);
                    } else {
                        log.warn("User with email {} is not active or does not belong to the user", email);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining user with email: {}", email);
        }
        return null;
    }

    @Override
    public User createUser(User user) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isBlank()) {
                log.info("Username can't be empty");
                return null;
            }
            if (user.getEmail() == null || user.getEmail().trim().isBlank()) {
                log.info("Email can't be empty");
                return null;
            }
            if (user.getPassword() == null || user.getPassword().trim().isBlank()) {
                log.info("Password can't be empty");
                return null;
            }
            UserDb userDb = new UserDb();
            userDb.setUsername(user.getUsername());
            userDb.setPassword(passwordEncoder.encode(user.getPassword()));
            userDb.setEmail(user.getEmail());
            userDb.setComment("");
            userDb.setRecordDate(OffsetDateTime.now().toLocalDateTime());
            userDb.setStatus(statusUtils.getActiveStatus());
            userDb.setRole(roleUtils.getUserRole());
            var userSaved = userDbRepository.save(userDb);
            return userMapper.userDbToUser(userSaved);
        } catch (Exception e) {
            log.error("Error creating user");
            return null;
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            var userDb = userDbRepository.findById(id).orElse(null);
            if (userDb != null && (authUtils.isUserAdmin() || id.equals(authUtils.getUser().getId()))) {
                userDb.setStatus(statusUtils.getDeletedStatus());
                userDbRepository.save(userDb);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting user with id: {}", id);
        }
        return false;
    }

    @Override
    public User updateUser(User user) {
        try {
            var userDb = userDbRepository.findById(user.getId()).orElse(null);
            if (userDb != null) {
                if (!authUtils.isUserAdmin() && !userDb.getId().equals(authUtils.getUser().getId())) {
                    log.warn("User does not have permission to update this user");
                    return null;
                }

                if (user.getUsername() != null && !user.getUsername().trim().isBlank()) {
                    userDb.setUsername(user.getUsername());
                }
                if (user.getPassword() != null && !user.getPassword().trim().isBlank()) {
                    userDb.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                if (user.getEmail() != null && !user.getEmail().trim().isBlank()) {
                    userDb.setEmail(user.getEmail());
                }

                return userMapper.userDbToUser(userDbRepository.save(userDb));
            }
        } catch (Exception e) {
            log.error("Error updating user with id: {}", user.getId());
        }
        return null;
    }
}
