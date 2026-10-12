package net.aldane.cash_balance;

import net.aldane.cash_balance.config.jwt.JwtUtil;
import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.security.model.UserDetails;
import net.aldane.cash_balance.service.AuthServiceImpl;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.AuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceLoginTest {
    AuthenticationManager manager = mock(AuthenticationManager.class);
    JwtUtil jwt = mock(JwtUtil.class);
    AuthServiceImpl service = new AuthServiceImpl(mock(UserDbRepository.class), mock(PasswordEncoder.class), mock(AuthUtils.class), manager, jwt);

    @Test void authenticatesCredentialsAndIssuesTokenForAuthenticatedPrincipal() {
        var principal = new UserDetails(new UserDb(), List.of());
        when(manager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(principal, null, List.of()));
        when(jwt.generateToken(principal)).thenReturn("issued-token");
        var response = service.login(new AuthRequest().username("alice").password("secret"));
        assertEquals("issued-token", response.getToken());
        verify(manager).authenticate(argThat(auth -> auth.getName().equals("alice") && auth.getCredentials().equals("secret")));
    }

    @Test void invalidCredentialsReturn401WithoutIssuingToken() {
        when(manager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> service.login(new AuthRequest().username("alice").password("wrong"))).getStatusCode().value());
        verifyNoInteractions(jwt);
    }
}
