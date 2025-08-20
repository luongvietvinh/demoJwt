package com.example.demo.entity;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    private String userId;
    private String userName;
    private String passWord;
    private String mail;
    private Set<String> roles;
    private Boolean isEnabled;

}
