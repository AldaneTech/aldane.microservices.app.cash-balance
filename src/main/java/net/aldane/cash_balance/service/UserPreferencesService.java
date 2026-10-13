package net.aldane.cash_balance.service;

import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.utils.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserPreferencesService {
    public record Preferences(String language) {}
    private final UserDbRepository users;
    private final AuthUtils auth;
    public UserPreferencesService(UserDbRepository users, AuthUtils auth) { this.users = users; this.auth = auth; }
    private UserDb currentUser() {
        var current = auth.getUser();
        if (current == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return users.findById(current.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
    @Transactional(readOnly = true)
    public Preferences get() { return new Preferences(currentUser().getLanguage()); }
    @Transactional
    public Preferences update(Preferences preferences) {
        if (preferences == null || !("es".equals(preferences.language()) || "en".equals(preferences.language()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported language");
        }
        var user = currentUser();
        user.setLanguage(preferences.language());
        users.save(user);
        return new Preferences(user.getLanguage());
    }
}
