package net.aldane.cash_balance;

import net.aldane.cash_balance.config.jwt.JwtUtil;
import net.aldane.cash_balance.mapper.UserMapper;
import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.repository.db.entity.*;
import net.aldane.cash_balance.security.model.UserDetails;
import net.aldane.cash_balance.service.AuthServiceImpl;
import net.aldane.cash_balance.service.UserServiceImpl;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.ChangePasswordRequest;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {
    UserDbRepository users = mock(UserDbRepository.class);
    AuthUtils auth = mock(AuthUtils.class);
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    AuthServiceImpl service = new AuthServiceImpl(users, encoder, auth, mock(org.springframework.security.authentication.AuthenticationManager.class), mock(JwtUtil.class));
    UserDb user;
    @BeforeEach void setup() {
        user = new UserDb(); user.setId(1L); user.setUsername("alice"); user.setPassword(encoder.encode("old-password"));
        var active = new StatusDb(); active.setId(1L); var role = new RoleDb(); role.setStatus(active); user.setStatus(active); user.setRole(role);
        when(auth.getUser()).thenReturn(user); when(users.findById(1L)).thenReturn(Optional.of(user));
        when(users.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        ReflectionTestUtils.setField(service, "em", mock(jakarta.persistence.EntityManager.class));
    }
    private ChangePasswordRequest request(String current, String next) { return new ChangePasswordRequest().currentPassword(current).newPassword(next); }
    @Test void meReturnsOnlyOwnProfileWithoutPassword() {
        var userService = new UserServiceImpl(users, Mappers.getMapper(UserMapper.class), encoder);
        ReflectionTestUtils.setField(userService, "authUtils", auth);
        assertEquals("alice", userService.getMe().getUsername());
        assertNull(userService.getMe().getPassword()); verify(users, never()).save(any());
    }
    @Test void incorrectCurrentPasswordNeverWrites() {
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.changePassword(request("incorrect", "new-password"))).getStatusCode().value());
        verify(users, never()).save(any());
    }
    @Test void rejectsWeakSameAndBcryptTruncatedPasswords() {
        for (String next : List.of("short", "old-password", "ñ".repeat(37), " ".repeat(10))) {
            assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.changePassword(request("old-password", next))).getStatusCode().value());
        }
        verify(users, never()).save(any());
    }
    @Test void changesPasswordAndInvalidatesAllOldTokens() {
        var jwt = new JwtUtil(); ReflectionTestUtils.setField(jwt, "secret", "test-secret-with-at-least-32-bytes-long"); jwt.init();
        var details = new UserDetails(user, List.of()); String oldToken = jwt.generateToken(details);
        assertTrue(jwt.validateToken(oldToken, details));
        service.changePassword(request("old-password", "new-password"));
        assertTrue(encoder.matches("new-password", user.getPassword())); assertFalse(encoder.matches("old-password", user.getPassword()));
        assertFalse(jwt.validateToken(oldToken, details)); assertTrue(jwt.validateToken(jwt.generateToken(details), details));
        verify(users).save(user);
    }
}
