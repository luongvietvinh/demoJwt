package com.example.demo.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class AuthResponse {
    // hai loại token
    private String accessToken;
    private String refreshToken;

    // tiện lợi phía client
    private String tokenType; // "Bearer"
    private long expiresIn;   // (ms) của access token

    // thông tin user kèm theo
    private UserInfo user;
}
