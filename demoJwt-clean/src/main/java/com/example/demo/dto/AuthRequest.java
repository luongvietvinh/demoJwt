package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 📨 Body cho /auth/login
@Data
@AllArgsConstructor
public class AuthRequest {
    private String userName;
    
    private String passWord;
}
