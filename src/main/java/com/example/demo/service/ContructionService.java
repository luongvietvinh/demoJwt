package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.ContructionDto;
import com.example.demo.dto.FileDiffResult;
import com.example.demo.dto.FileDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ContructionRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.impl.FileSyncCommonService;
import com.example.demo.service.impl.S3FileService;
import com.example.demo.service.mail.EmailService;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContructionService implements IcontructionService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private EmailService emailService;
  private S3FileService s3FileService;
  private ContructionRepository repository;
  private FileRepository fileRepository;
  private FileSyncCommonService fileSyncService;

  @Transactional
  @CacheEvict(value = "constructionList", allEntries = true)
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
    if(construction == null) {
      throw new NotFoundException(MessageUtils.CONTRUCTION_NOT_FOUND);
    }
    return Optional.of(construction);
  }

  @Override
  @Cacheable(value = "constructionList", key = "#page + '-' + #size")
  public List<ContructionDto> getListContruction(int page, int size) {
    if (page == 0) {
      page++;
    }
    int offset = (page - 1) * size;
    List<ContructionDto> contructions = repository.getLisstContruction(size, offset);

    return contructions;
  }

  @Override
  @Transactional
  @CacheEvict(value = "constructionList", allEntries = true)
  public void deleteContruction(String contructionId) {
    logger.info("Request to delete construction with id={}", contructionId);

    ContructionDto construction = repository.getContructionById(contructionId);
    if (construction == null) {
      logger.warn("Construction with id={} not found", contructionId);
        throw new NotFoundException(MessageUtils.CONTRUCTION_NOT_FOUND);
    }

    // Xóa construction
    repository.deleteContruction(contructionId);
    logger.info("Deleted construction record with id={}", contructionId);

    // Xử lý file nếu có
    if (construction.getFiles() != null && !construction.getFiles().isEmpty()) {
            // Xóa trên S3
            s3FileService.deleteFiles(construction.getFiles());
            logger.info("Deleted {} files from S3 for construction id={}", construction.getFiles().size(), contructionId);

            // Xóa trong DB
            List<String> uuids = construction.getFiles().stream()
                                        .map(FileDto::getUuId)
                                        .toList();
            fileRepository.deleteFilesByUuids(uuids);
            logger.info("Deleted {} files from DB for construction id={}", uuids.size(), contructionId);
    }

  }

  @Transactional
  @CacheEvict(value = "constructionList", allEntries = true)
  public ContructionDto updateContruction(ContructionDto dto) {
      FileDiffResult diff = new FileDiffResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
      try {
          if (!checkExistsContruction(dto.getContructionId())) {
              throw new NotFoundException(MessageUtils.CONTRUCTION_NOT_FOUND);
          }

          diff = fileSyncService.syncFiles(dto);

          int updated = repository.updateContruction(dto);
          if (updated <= 0) throw new NotFoundException(MessageUtils.UPDATE_FAIL);

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

  @Transactional
  public int deleteOldData() {
      LocalDateTime threshold = LocalDateTime.now().minusDays(1);
      return repository.deleteOlderThan(threshold);
  }

}
