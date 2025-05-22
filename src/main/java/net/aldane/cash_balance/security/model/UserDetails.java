package net.aldane.cash_balance.security.model;

import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance_api_server_java.model.Role;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private UserDb user;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetails(UserDb user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public UserDb getUser() {
        return user;
    }

    public void setUser(UserDb user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return getUser().getPassword();
    }

    @Override
    public String getUsername() {
        return getUser().getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getUser().getStatus().getId() == 1;
    }


}