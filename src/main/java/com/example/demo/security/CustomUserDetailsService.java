package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserMapper;

// Load user theo username (JPA). Nếu bạn dùng MyBatis, gọi mapper ở đây.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userRepository;

    @Override public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users u = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + userName));
        return new CustomUserDetails(u);
    }
}
