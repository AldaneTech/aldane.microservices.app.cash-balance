package net.aldane.cash_balance.controller.admin;

import jakarta.validation.Valid;
import net.aldane.cash_balance.config.jwt.JwtUtil;
import net.aldane.cash_balance.security.service.UserDetailsServiceImpl;
import net.aldane.cash_balance_api_server_java.api.AuthApi;
import net.aldane.cash_balance_api_server_java.model.AuthRequest;
import net.aldane.cash_balance_api_server_java.model.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    public ResponseEntity<AuthResponse> login(@Valid AuthRequest authRequest) {
        // Autenticar al usuario
        org.springframework.security.core.Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401).build();
        }

        // Generar el token JWT
        String token = jwtUtil.generateToken((net.aldane.cash_balance.security.model.UserDetails) authentication.getPrincipal());
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        // Retornar la respuesta con el token
        return ResponseEntity.ok(authResponse);
    }
}

