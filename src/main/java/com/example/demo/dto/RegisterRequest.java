package com.example.demo.dto;

import lombok.Data;

import java.util.Set;
import com.example.demo.entity.RoleEntity;

// 📨 Body cho /auth/register
@Data
public class RegisterRequest {
    private String userName;
    private String passWord;
    // ví dụ: ["ROLE_USER"] hoặc ["ROLE_ADMIN","ROLE_USER"]
    private Set<String> roles;
}
