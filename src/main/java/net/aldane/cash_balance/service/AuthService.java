package net.aldane.cash_balance.service;

import net.aldane.cash_balance_api_server_java.model.AuthRequest;
import net.aldane.cash_balance_api_server_java.model.AuthResponse;
import net.aldane.cash_balance_api_server_java.model.ChangePasswordRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    void changePassword(ChangePasswordRequest request);
}
