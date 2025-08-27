package com.example.demo.dto;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.request.ContructionReigisterRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 📨 Body cho /auth/register
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContructionDto {
    private String userID;
    private String userName;
    private String contructionId;
    private String contructionName;
    private String address;
    private String phoneNumber;
    private String zipCode;
    private String mail;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<FileDto> files;
    
    // Ẩn field này khi serialize JSON
    @JsonIgnore  
    private List<MultipartFile> uploadFiles;
    
 // 🛠️ Method chuyển từ request sang DTO
    public static ContructionDto fromRequest(ContructionReigisterRequest request,String contructionId,String userId,String username) {
      return ContructionDto.builder()
              .userID(userId)
              .userName(username)
              .contructionId(contructionId)
              .contructionName(request.getContructionName())
              .address(request.getAddress())
              .phoneNumber(request.getPhoneNumber())
              .zipCode(request.getZipCode())
              .mail(request.getMail())
              .uploadFiles(request.getUploadFiles())
              .build();
  }
    
}
