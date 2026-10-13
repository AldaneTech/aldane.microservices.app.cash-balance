package net.aldane.cash_balance.controller;

import net.aldane.cash_balance.service.UserPreferencesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/preferences")
public class UserPreferencesController {
    private final UserPreferencesService preferences;
    public UserPreferencesController(UserPreferencesService preferences) { this.preferences = preferences; }
    @GetMapping
    public UserPreferencesService.Preferences get() { return preferences.get(); }
    @PutMapping
    public UserPreferencesService.Preferences update(@RequestBody UserPreferencesService.Preferences request) { return preferences.update(request); }
}
