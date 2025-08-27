package com.example.demo.service.impl;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.dto.FileDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3FileService {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public List<FileDto> uploadFiles(List<MultipartFile> files, String userId, String userName) {
        List<FileDto> fileDtos = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                FileDto fileDto = uploadSingleFile(file, userId, userName);
                if (fileDto != null) {
                    fileDtos.add(fileDto);
                }
            }
        }
        
        return fileDtos;
    }
    
    public String generatePresignedUrl(String s3Key, int expireMinutes) {
      Date expiration = new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000);

      GeneratePresignedUrlRequest generatePresignedUrlRequest =
              new GeneratePresignedUrlRequest(bucketName, s3Key)
                      .withMethod(HttpMethod.GET)
                      .withExpiration(expiration);

      URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
      return url.toString();
  }

    private FileDto uploadSingleFile(MultipartFile file, String userId, String userName) {
        try {
            // Tạo unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // Tạo folder structure: userId/constructionId/filename
            String s3Key = String.format("constructions/%s/%s", userId, uniqueFilename);
            
            // Set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            // Upload to S3
            PutObjectRequest request = new PutObjectRequest(bucketName, s3Key, 
                    file.getInputStream(), metadata);
            s3Client.putObject(request);
            
            log.info("File uploaded successfully: {}", s3Key);
            
            // Tạo FileDto để lưu vào DB
            String uuid = UUID.randomUUID().toString();
            FileDto fileDto = FileDto.builder()
                .uuId(uuid)
                .userId(userId)
                .userName(userName)
                .fileName(originalFilename)
                .fileSize(file.getSize())
                .filePath(s3Key)
                .createTime(new java.sql.Timestamp(System.currentTimeMillis()))
                .build();
                
            return fileDto;
            
        } catch (IOException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            return null;
        }
    }

    public String getFileUrl(String s3Key) {
        return s3Client.getUrl(bucketName, s3Key).toString();
    }

    public boolean deleteFile(String s3Key) {
        try {
            s3Client.deleteObject(bucketName, s3Key);
            log.info("File deleted successfully: {}", s3Key);
            return true;
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            return false;
        }
    }
}