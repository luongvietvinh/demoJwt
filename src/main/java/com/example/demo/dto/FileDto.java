package com.example.demo.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 📨 Body cho /auth/register
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDto {

    private String uuId;
    private String userId;
    private String constructionId;
    private String userName;
    private String fileName;
    private Long fileSize;
    private String filePath;
    private Timestamp createTime;
    private Timestamp updateTime;
}
