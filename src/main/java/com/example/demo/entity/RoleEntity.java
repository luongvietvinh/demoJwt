package com.example.demo.entity;

import java.sql.Timestamp;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
    private String userId;
    private String userName;
    private String role;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp deleteTime;
    private Boolean isActive;
}
