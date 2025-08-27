package com.example.demo.entity;

import java.sql.Timestamp;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContructionEntity {
    private String contructionId;
    private String userId;
    private String userName;
    private String contructionName;
    private String address;
    private String phoneNumber;
    private String zipCode;
    private String mail;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
