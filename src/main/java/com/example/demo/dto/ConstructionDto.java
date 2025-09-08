package com.example.demo.dto;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.request.ConstructionReigisterRequest;
import com.example.demo.dto.request.ContructionUpdateRequest;
import com.example.demo.utils.CustomTimestampDeserializer;
import com.example.demo.utils.CustomTimestampSerializer;
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
public class ConstructionDto {
    private String userID;
    private String userName;
    private String constructionId;
    private String constructionName;
    private String address;
    private String phoneNumber;
    private String zipCode;
    private String mail;
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp createdAt;
    @JsonSerialize(using = CustomTimestampSerializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp updatedAt;
    
    private List<FileDto> files;
    
    // Ẩn field này khi serialize JSON
    @JsonIgnore  
    private List<MultipartFile> uploadFiles;
    
 // 🛠️ Method chuyển từ request sang DTO
    public static ConstructionDto fromRequest(ConstructionReigisterRequest request,String constructionId,String userId,String username) {
      return ConstructionDto.builder()
              .userID(userId)
              .userName(username)
              .constructionId(constructionId)
              .constructionName(request.getConstructionName())
              .address(request.getAddress())
              .phoneNumber(request.getPhoneNumber())
              .zipCode(request.getZipCode())
              .mail(request.getMail())
              .uploadFiles(request.getUploadFiles())
              .build();
  }
    // 🛠️ Method chuyển từ request sang DTO
    public static ConstructionDto fromRequest(ContructionUpdateRequest request,String userId,String username) {
      return ConstructionDto.builder()
          .userID(userId)
          .userName(username)
          .constructionId(request.getConstructionId())
          .constructionName(request.getConstructionName())
          .address(request.getAddress())
          .phoneNumber(request.getPhoneNumber())
          .zipCode(request.getZipCode())
          .mail(request.getMail())
          .uploadFiles(request.getUploadFiles())
          .updatedAt(DateTimeUtils.toTimestamp(request.getUpdatedAt()))
          .build();
    }
    
}
