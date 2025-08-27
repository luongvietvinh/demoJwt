package com.example.demo.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.ContructionDto;
import com.example.demo.dto.FileDto;
import com.example.demo.repository.ContructionRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.impl.S3FileService;
import com.example.demo.service.mail.EmailService;

@Service
public class ContructionService implements IcontructionService {


  private static final Logger logger = LoggerFactory.getLogger(UserService.class);


  @Autowired
  private EmailService emailService;
  @Autowired
  private S3FileService s3FileService;
  @Autowired
  private ContructionRepository repository;
  @Autowired
  private FileRepository fileRepository;

  // private String generateUserId() {
  // StringBuilder sb = new StringBuilder(6);
  // for (int i = 0; i < 6; i++) {
  // sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
  // }
  // return sb.toString();
  // }

  // @Override
  // @Transactional
  // public ContructionDto createContruction(ContructionDto dto) {
  // int count = repository.createContruction(dto);
  // if(count <= 0) {
  // throw new IllegalArgumentException("Inser thất bại");
  // }
  // fileRepository.insertListFile(dto.getFiles());
  // ContructionDto contruction = repository.getContructionById(dto.getContructionId());
  // return contruction;
  // }


  @Transactional
  public ContructionDto createContruction(ContructionDto dto) {
    List<FileDto> uploadedFiles = Collections.emptyList();

    try {
      // Upload files
      if (dto.getUploadFiles() != null && !dto.getUploadFiles().isEmpty()) {
        uploadedFiles =
            s3FileService.uploadFiles(dto.getUploadFiles(), dto.getUserID(), dto.getUserName());
        dto.setFiles(uploadedFiles);
      }
      logger.info("ContructionId ở DTO trước khi insert: " + dto.getContructionId());

      // Insert construction
      int count = repository.createContruction(dto);
      if (count <= 0)
        throw new IllegalArgumentException("Insert construction thất bại");

      // Insert files
      if (!uploadedFiles.isEmpty()) {
        int fileCount = fileRepository.insertListFile(uploadedFiles, dto.getContructionId());
        if (fileCount <= 0)
          throw new IllegalArgumentException("Insert files thất bại");
      }

      ContructionDto construction = repository.getContructionById(dto.getContructionId());

      // Gửi mail
      sendRegistrationEmail(dto);

      return construction;

    } catch (Exception e) {
      // Rollback files S3 nếu có lỗi
      cleanupUploadedFiles(uploadedFiles);
      throw e;
    }
  }

  private void sendRegistrationEmail(ContructionDto dto) {
    try {
      Map<String,Object> variables = new HashMap<>();
      String subject = "Đăng ký công trình thành công";
      variables.put("userName", dto.getUserName());
      variables.put("contructionId", dto.getContructionId());
      variables.put("contructionName", dto.getContructionName());
      variables.put("subject", subject);
      
      String template = "MailContruction";
      
      emailService.sendEmail(dto.getMail(), subject,
          template,variables );
      logger.info("SEND MAIL success to user {}", dto.getUserName());
    } catch (Exception e) {
      logger.error("Không thể gửi email chào mừng cho user: {}", dto.getUserName(), e);
    }
  }

  private void cleanupUploadedFiles(List<FileDto> files) {
    if (files == null || files.isEmpty())
      return;
    files.forEach(file -> {
      try {
        s3FileService.deleteFile(file.getFilePath());
      } catch (Exception ex) {
        logger.warn("Không thể xóa file S3 {} sau rollback", file.getFilePath(), ex);
      }
    });
  }



  @Override
  public Optional<ContructionDto> getContructionById(String contructionId) {
    ContructionDto construction = repository.getContructionById(contructionId);
    return Optional.of(construction);
  }

  @Override
  public List<ContructionDto> getListContruction(int page, int size) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteContruction(String contructionId) {
    // TODO Auto-generated method stub

  }

  public ContructionDto updateContruction(ContructionDto dto) {
    // TODO Auto-generated method stub
    return null;
  }
}
