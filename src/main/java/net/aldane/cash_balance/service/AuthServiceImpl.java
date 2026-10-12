package net.aldane.cash_balance.service;

import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;

@Service
public class AuthServiceImpl implements AuthService {
    @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager em;
    private final UserDbRepository users;
    private final PasswordEncoder encoder;
    private final AuthUtils auth;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;
    private final net.aldane.cash_balance.config.jwt.JwtUtil jwtUtil;

    public AuthServiceImpl(UserDbRepository users, PasswordEncoder encoder, AuthUtils auth,
            org.springframework.security.authentication.AuthenticationManager authenticationManager,
            net.aldane.cash_balance.config.jwt.JwtUtil jwtUtil) {
        this.users = users;
        this.encoder = encoder;
        this.auth = auth;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        org.springframework.security.core.Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        var principal = (net.aldane.cash_balance.security.model.UserDetails) authentication.getPrincipal();
        return new AuthResponse().token(jwtUtil.generateToken(principal));
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String current = request.getCurrentPassword(), next = request.getNewPassword();
        if (current == null || current.isEmpty() || next == null || next.isBlank()
                || next.codePointCount(0, next.length()) < 8 || next.getBytes(StandardCharsets.UTF_8).length > 72
                || current.getBytes(StandardCharsets.UTF_8).length > 72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var user = users.findByIdForUpdate(auth.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        em.refresh(user);
        if (!encoder.matches(current, user.getPassword())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (encoder.matches(next, user.getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        user.setPassword(encoder.encode(next));
        users.save(user);
    }
}
