package com.example.demo.dto;

import lombok.*;

import java.util.Set;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class UserInfo {
    private String userId;
    private String username;
    private Set<String> roles;
}
