package com.example.demo.dto;

import lombok.Data;

import java.util.Set;

// Body đăng ký: roles nhận theo enum name, ví dụ: ["USER","ADMIN"]
@Data
public class LoginRequestDto {
    private String userName;
    private String passWord;
    private Set<String> roles; // có thể null -> mặc định USER
}