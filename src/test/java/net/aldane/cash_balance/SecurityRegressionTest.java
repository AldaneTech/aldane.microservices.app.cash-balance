package net.aldane.cash_balance;

import net.aldane.cash_balance.config.jwt.*;
import net.aldane.cash_balance.mapper.*;
import net.aldane.cash_balance.repository.db.entity.*;
import net.aldane.cash_balance.security.model.UserDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityRegressionTest {
    private JwtUtil jwt() {
        var jwt = new JwtUtil();
        ReflectionTestUtils.setField(jwt, "secret", "test-secret-with-at-least-32-bytes-long");
        jwt.init(); return jwt;
    }

    @Test void passwordHashNeverLeavesUserMapper() {
        var user = new UserDb(); user.setPassword("sensitive-hash");
        assertNull(Mappers.getMapper(UserMapper.class).userDbToUser(user).getPassword());
    }

    @Test void malformedTokenReturns401WithoutContinuingChain() throws Exception {
        var filter = new JwtFilter(); ReflectionTestUtils.setField(filter, "jwtUtil", jwt());
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer invalid");
        var response = new MockHttpServletResponse(); var chain = mock(jakarta.servlet.FilterChain.class);
        filter.doFilter(request, response, chain);
        assertEquals(401, response.getStatus()); verifyNoInteractions(chain);
    }

    @Test void disabledAccountCannotUsePreviouslyIssuedToken() {
        var jwt = jwt(); var details = mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(details.getUsername()).thenReturn("alice"); when(details.isEnabled()).thenReturn(false);
        assertFalse(jwt.validateToken(jwt.generateToken("alice"), details));
    }

    @Test void activeAccountAndRoleAreRequired() {
        var user = new UserDb(); var active = new StatusDb(); active.setId(1L);
        var deleted = new StatusDb(); deleted.setId(2L);
        var role = new RoleDb(); role.setStatus(active); user.setRole(role); user.setStatus(active);
        var details = new UserDetails(user, List.of()); assertTrue(details.isEnabled());
        user.setStatus(deleted); assertFalse(details.isEnabled());
        user.setStatus(active); role.setStatus(deleted); assertFalse(details.isEnabled());
        user.setStatus(null); assertFalse(details.isEnabled());
    }

    @Test void reusedUsernameCannotInheritAnotherAccountsToken() {
        var jwt = jwt();
        var active = new StatusDb(); active.setId(1L);
        var role = new RoleDb(); role.setStatus(active);
        var first = new UserDb(); first.setId(1L); first.setUsername("alice"); first.setStatus(active); first.setRole(role);
        var second = new UserDb(); second.setId(2L); second.setUsername("alice"); second.setStatus(active); second.setRole(role);
        var firstDetails = new UserDetails(first, List.of());
        var token = jwt.generateToken(firstDetails);
        assertTrue(jwt.validateToken(token, firstDetails));
        assertFalse(jwt.validateToken(token, new UserDetails(second, List.of())));
        assertFalse(jwt.validateToken(jwt.generateToken("alice"), firstDetails));
    }

    @Test void anonymousAuthorizationChecksFailClosed() {
        SecurityContextHolder.clearContext();
        assertFalse(new net.aldane.cash_balance.utils.AuthValidator().isOwnerOrAdmin(1L));
        assertFalse(new net.aldane.cash_balance.utils.AuthUtils().isUserAdmin());
        assertNull(new net.aldane.cash_balance.utils.AuthUtils().getUser());
    }

    @Test void timestampConversionPreservesInstant() {
        var original = java.time.OffsetDateTime.parse("2026-10-11T15:00:00+02:00");
        var mapper = Mappers.getMapper(UserMapper.class);
        assertEquals(original.toInstant(), mapper.localDateTimeToOffsetDateTimeUser(
                mapper.offsetDateTimeToLocalDateTimeUser(original)).toInstant());
    }
}
