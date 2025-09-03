package com.example.demo.dto;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.request.ContructionReigisterRequest;
import com.example.demo.dto.request.ContructionUpdateRequest;
import com.example.demo.utils.CustomTimestampDeserializer;
import com.example.demo.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp createdAt;
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
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
    // 🛠️ Method chuyển từ request sang DTO
    public static ContructionDto fromRequest(ContructionUpdateRequest request,String userId,String username) {
      return ContructionDto.builder()
          .userID(userId)
          .userName(username)
          .contructionId(request.getContructionId())
          .contructionName(request.getContructionName())
          .address(request.getAddress())
          .phoneNumber(request.getPhoneNumber())
          .zipCode(request.getZipCode())
          .mail(request.getMail())
          .uploadFiles(request.getUploadFiles())
          .updatedAt(DateTimeUtils.toTimestamp(request.getUpdateAt()))
          .build();
    }
    
}
