package com.example.demo.dto;

import lombok.Data;

// 📨 Body cho /auth/login
@Data
public class AuthRequest {
    private String userName;
    
    private String passWord;
}
