package net.aldane.cash_balance;

import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.service.UserPreferencesService;
import net.aldane.cash_balance.utils.AuthUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPreferencesServiceTest {
    private final UserDbRepository users = mock(UserDbRepository.class);
    private final AuthUtils auth = mock(AuthUtils.class);
    private final UserPreferencesService service = new UserPreferencesService(users, auth);

    @Test void defaultsToSpanishAndPersistsOnlyTheAuthenticatedUsersPreference() {
        var principal = new UserDb(); principal.setId(7L);
        var stored = new UserDb(); stored.setId(7L);
        when(auth.getUser()).thenReturn(principal);
        when(users.findById(7L)).thenReturn(Optional.of(stored));
        assertEquals("es", service.get().language());
        assertEquals("en", service.update(new UserPreferencesService.Preferences("en")).language());
        assertEquals("en", service.get().language());
        assertEquals("es", principal.getLanguage());
        verify(users).save(stored);
        verify(users, never()).findById(8L);
    }
    @Test void rejectsUnsupportedOrEmptyLanguagesWithoutWriting() {
        for (var language : new String[]{"fr", "", "ES", null}) {
            var exception = assertThrows(ResponseStatusException.class,
                    () -> service.update(new UserPreferencesService.Preferences(language)));
            assertEquals(400, exception.getStatusCode().value());
        }
        assertThrows(ResponseStatusException.class, () -> service.update(null));
        verifyNoInteractions(users);
    }
    @Test void requiresAnAuthenticatedExistingUser() {
        assertEquals(401, assertThrows(ResponseStatusException.class, service::get).getStatusCode().value());
        var principal = new UserDb(); principal.setId(7L);
        when(auth.getUser()).thenReturn(principal);
        when(users.findById(7L)).thenReturn(Optional.empty());
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> service.update(new UserPreferencesService.Preferences("en"))).getStatusCode().value());
        verify(users, never()).save(any());
    }
}
