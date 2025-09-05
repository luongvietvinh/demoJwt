package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Users;
import com.example.demo.exception.NotFoundException;
import com.example.demo.service.UserService;

// Load user theo username (JPA). Nếu bạn dùng MyBatis, gọi mapper ở đây.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override 
    public UserDetails loadUserByUsername(String userName) throws NotFoundException {
        Users u = userService.getUserByName(userName);
        return new CustomUserDetails(u);
    }
}
