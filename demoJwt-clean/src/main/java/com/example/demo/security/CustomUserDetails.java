package com.example.demo.security;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.demo.Role;
import com.example.demo.entity.Users;

// Convert User -> UserDetails
public class CustomUserDetails implements UserDetails {

    private final Users user;

    public CustomUserDetails(Users user) { this.user = user; }

    public String getUserId() { return user.getUserId(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_ prefix để Spring Security hiểu
      return user.getRoles().stream()
          .map(Role::fromValue)                  // value -> enum
          .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) // Spring Security cần prefix "ROLE_"
          .collect(Collectors.toSet());
    }

    @Override public String getPassword() { return user.getPassWord(); }
    @Override public String getUsername() { return user.getUserName(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() {return Boolean.TRUE.equals(user.getIsEnabled());
    }
}
