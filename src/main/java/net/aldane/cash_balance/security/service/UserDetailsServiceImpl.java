package net.aldane.cash_balance.security.service;

import net.aldane.cash_balance.mapper.RoleMapper;
import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.security.model.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDbRepository userDbRepository;
    private final RoleMapper roleMapper;

    public UserDetailsServiceImpl(UserDbRepository userDbRepository, RoleMapper roleMapper){
        this.userDbRepository = userDbRepository;
        this.roleMapper = roleMapper;
    }
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userDbRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        return new UserDetails(user, authorities);
    }

}