package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.services.kms.model.transform.NotFoundExceptionUnmarshaller;
import com.example.demo.dto.ContructionDto;
import com.example.demo.dto.FileDiffResult;
import com.example.demo.dto.FileDto;
import com.example.demo.entity.Users;
import com.example.demo.repository.ContructionRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.impl.FileSyncCommonService;
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
  @Autowired
  private FileSyncCommonService fileSyncService;

  @Transactional
  public ContructionDto createContruction(ContructionDto dto) {
      List<FileDto> uploadedFiles = Collections.emptyList();
      try {
          if (checkExistsContruction(dto.getContructionId())) {
              throw new IllegalArgumentException("Công trình đã tồn tại với ID: " + dto.getContructionId());
          }

          if (dto.getUploadFiles() != null && !dto.getUploadFiles().isEmpty()) {
              uploadedFiles = s3FileService.uploadFiles(dto.getUploadFiles(), dto.getUserID(), dto.getUserName(), dto.getContructionId());
              dto.setFiles(uploadedFiles);
          }

          int count = repository.createContruction(dto);
          if (count <= 0) throw new IllegalArgumentException("Insert construction thất bại");

          if (!uploadedFiles.isEmpty()) {
              int fileCount = fileRepository.insertListFile(uploadedFiles, dto.getContructionId());
              if (fileCount <= 0) throw new IllegalArgumentException("Insert files thất bại");
          }

          sendRegistrationEmail(dto);
          return repository.getContructionById(dto.getContructionId());

      } catch (Exception e) {
          s3FileService.deleteFiles(uploadedFiles); // rollback
          throw e;
      }
  }
  
  /*/ 
   * check exit contruction
   */
  private boolean checkExistsContruction(String contructionId) {
    boolean existsByContructionId = repository.existsByContructionId(contructionId);
    return existsByContructionId;
  }

  private void sendRegistrationEmail(ContructionDto dto) {
    try {
      Map<String, Object> variables = new HashMap<>();
      String subject = "Đăng ký công trình thành công";
      variables.put("userName", dto.getUserName());
      variables.put("contructionId", dto.getContructionId());
      variables.put("contructionName", dto.getContructionName());
      variables.put("subject", subject);

      String template = "MailContruction";

      emailService.sendEmail(dto.getMail(), subject,
          template, variables);
      logger.info("SEND MAIL success to user {}", dto.getUserName());
    } catch (Exception e) {
      logger.error("Không thể gửi email chào mừng cho user: {}", dto.getUserName(), e);
    }
  }


  @Override
  public Optional<ContructionDto> getContructionById(String contructionId) {
    ContructionDto construction = repository.getContructionById(contructionId);
    return Optional.of(construction);
  }

  @Override
  public List<ContructionDto> getListContruction(int page, int size) {
    if (page == 0) {
      page++;
    }
    int offset = (page - 1) * size;
    List<ContructionDto> contructions = repository.getLisstContruction(size, offset);

    return contructions;
  }

  @Override
  public void deleteContruction(String contructionId) {
    // TODO Auto-generated method stub

  }

  @Transactional
  public ContructionDto updateContruction(ContructionDto dto) throws Exception {
      FileDiffResult diff = new FileDiffResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
      try {
          if (!checkExistsContruction(dto.getContructionId())) {
              throw new NotFoundException("Công trình không tồn tại: " + dto.getContructionId());
          }

          diff = fileSyncService.syncFiles(dto);

          int updated = repository.updateContruction(dto);
          if (updated <= 0) {
            throw new OptimisticLockingFailureException("Dữ liệu đã bị thay đổi bởi người khác, vui lòng tải lại!");
          }

          if (!diff.getToInsert().isEmpty()) {
              fileRepository.insertListFile(diff.getToInsert(), dto.getContructionId());
          }

          if (!diff.getToDelete().isEmpty()) {
              s3FileService.deleteFiles(diff.getToDelete());
              List<String> uuids = diff.getToDelete().stream().map(FileDto::getUuId).toList();
              fileRepository.deleteFilesByUuids(uuids);
          }

          return repository.getContructionById(dto.getContructionId());

      } catch (Exception e) {
          s3FileService.deleteFiles(diff.getUploadedNow()); // rollback file mới
          throw e;
      }
  }

}
