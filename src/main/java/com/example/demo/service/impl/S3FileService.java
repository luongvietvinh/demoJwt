package com.example.demo.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
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

    /**
     * Upload 1 list files mới (MultipartFile).
     * Không động chạm tới DB, chỉ upload lên S3 và build FileDto.
     */
    public List<FileDto> uploadFiles(List<MultipartFile> files, String userId, String userName, String contructionId) {
        if (files == null || files.isEmpty()) return Collections.emptyList();

        List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                FileDto fileDto = uploadSingleFile(file, userId, userName, contructionId);
                if (fileDto != null) fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }

    private FileDto uploadSingleFile(MultipartFile file, String userId, String userName, String contructionId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            String s3Key = String.format("constructions/%s/%s/%s", userId, contructionId, uniqueFilename);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            s3Client.putObject(new PutObjectRequest(bucketName, s3Key, file.getInputStream(), metadata));

            log.info("File uploaded successfully: {}", s3Key);

            return FileDto.builder()
                    .uuId(UUID.randomUUID().toString())
                    .userId(userId)
                    .contructionId(contructionId)
                    .userName(userName)
                    .fileName(originalFilename)
                    .fileSize(file.getSize())
                    .filePath(s3Key)
                    .createTime(new java.sql.Timestamp(System.currentTimeMillis()))
                    .build();

        } catch (IOException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            return null;
        }
    }

    public boolean deleteFiles(List<FileDto> files) {
        if (files == null || files.isEmpty()) return true;

        List<String> keys = files.stream().map(FileDto::getFilePath).collect(Collectors.toList());
        try {
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName).withKeys(keys.toArray(new String[0]));
            s3Client.deleteObjects(request);
            log.info("Deleted {} files from S3", keys.size());
            return true;
        } catch (Exception e) {
            log.error("Error deleting files from S3: {}", e.getMessage());
            return false;
        }
    }
}