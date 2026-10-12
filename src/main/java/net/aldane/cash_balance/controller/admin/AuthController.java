package net.aldane.cash_balance.controller.admin;

import jakarta.validation.Valid;
import net.aldane.cash_balance.service.AuthService;
import net.aldane.cash_balance_api_server_java.api.AuthApi;
import net.aldane.cash_balance_api_server_java.model.AuthRequest;
import net.aldane.cash_balance_api_server_java.model.AuthResponse;
import net.aldane.cash_balance_api_server_java.model.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AuthResponse> login(@Valid AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
