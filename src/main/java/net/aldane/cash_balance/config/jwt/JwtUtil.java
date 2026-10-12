package net.aldane.cash_balance.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return generateToken(username, null, null);
    }

    public String generateToken(net.aldane.cash_balance.security.model.UserDetails user) {
        return generateToken(user.getUsername(), user.getUser().getId(), credentialStamp(user.getPassword()));
    }

    private String generateToken(String username, Long userId, String credentials) {
        return Jwts.builder()
                .claim("uid", userId)
                .claim("credentials", credentials)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Expira en 10 horas
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        if (userDetails instanceof net.aldane.cash_balance.security.model.UserDetails user) {
            var id = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().get("uid", Long.class);
            if (!user.getUser().getId().equals(id)) return false;
            String stamp = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().get("credentials", String.class);
            if (stamp == null || !java.security.MessageDigest.isEqual(stamp.getBytes(StandardCharsets.UTF_8), credentialStamp(user.getPassword()).getBytes(StandardCharsets.UTF_8))) return false;
        }
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token)
                && userDetails.isEnabled() && userDetails.isAccountNonLocked()
                && userDetails.isAccountNonExpired() && userDetails.isCredentialsNonExpired());
    }

    public String getUsernameFromToken(String token) {
        var claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        if (claims.getSubject() == null || claims.getSubject().isBlank() || claims.getExpiration() == null) {
            throw new io.jsonwebtoken.MalformedJwtException("Missing required claims");
        }
        return claims.getSubject();
    }
    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    // A keyed fingerprint invalidates previous tokens without exposing the stored password hash.
    private String credentialStamp(String passwordHash) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(key);
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                    mac.doFinal((passwordHash == null ? "" : passwordHash).getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Cannot fingerprint credentials", e);
        }
    }
}

