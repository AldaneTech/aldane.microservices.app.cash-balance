package net.aldane.cash_balance.controller.admin;

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    // Getters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
