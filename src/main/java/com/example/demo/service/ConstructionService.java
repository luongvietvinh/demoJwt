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
import com.example.demo.dto.ConstructionDto;
import com.example.demo.dto.FileDiffResult;
import com.example.demo.dto.FileDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ConstructionRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.impl.FileSyncCommonService;
import com.example.demo.service.impl.S3FileService;
import com.example.demo.service.mail.EmailService;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConstructionService implements IconstructionService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final EmailService emailService;
  private final S3FileService s3FileService;
  private final ConstructionRepository repository;
  private final FileRepository fileRepository;
  private final FileSyncCommonService fileSyncService;

  @Transactional
  @CacheEvict(value = "constructionList", allEntries = true)
  public ConstructionDto createConstruction(ConstructionDto dto) {
      List<FileDto> uploadedFiles = Collections.emptyList();
      try {
          if (checkExistsConstruction(dto.getConstructionId())) {
              throw new IllegalArgumentException("Công trình đã tồn tại với ID: " + dto.getConstructionId());
          }

          if (dto.getUploadFiles() != null && !dto.getUploadFiles().isEmpty()) {
              uploadedFiles = s3FileService.uploadFiles(dto.getUploadFiles(), dto.getUserID(), dto.getUserName(), dto.getConstructionId());
              dto.setFiles(uploadedFiles);
          }

          int count = repository.createConstruction(dto);
          if (count <= 0) throw new IllegalArgumentException("Insert construction thất bại");

          if (!uploadedFiles.isEmpty()) {
              int fileCount = fileRepository.insertListFile(uploadedFiles, dto.getConstructionId());
              if (fileCount <= 0) throw new IllegalArgumentException("Insert files thất bại");
          }

          sendRegistrationEmail(dto);
          return repository.getConstructionById(dto.getConstructionId());

      } catch (Exception e) {
          s3FileService.deleteFiles(uploadedFiles); // rollback
          throw e;
      }
  }
  
  /*/ 
   * check exit construction
   */
  private boolean checkExistsConstruction(String constructionId) {
    boolean existsByConstructionId = repository.existsByConstructionId(constructionId);
    return existsByConstructionId;
  }

  private void sendRegistrationEmail(ConstructionDto dto) {
    try {
      Map<String, Object> variables = new HashMap<>();
      String subject = "Đăng ký công trình thành công";
      variables.put("userName", dto.getUserName());
      variables.put("constructionId", dto.getConstructionId());
      variables.put("constructionName", dto.getConstructionName());
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
  public Optional<ConstructionDto> getConstructionById(String constructionId) {
    ConstructionDto construction = repository.getConstructionById(constructionId);
    if(construction == null) {
      throw new NotFoundException(MessageUtils.CONTRUCTION_NOT_FOUND);
    }
    return Optional.of(construction);
  }

  @Override
  @Cacheable(value = "constructionList", key = "#page + '-' + #size")
  public List<ConstructionDto> getListConstruction(int page, int size) {
    if (page == 0) {
      page++;
    }
    int offset = (page - 1) * size;
    List<ConstructionDto> constructions = repository.getListConstruction(size, offset);

    return constructions;
  }

  @Override
  @Transactional
  @CacheEvict(value = "constructionList", allEntries = true)
  public void deleteConstruction(String constructionId) {
    logger.info("Request to delete construction with id={}", constructionId);

    ConstructionDto construction = repository.getConstructionById(constructionId);
    if (construction == null) {
      logger.warn("Construction with id={} not found", constructionId);
        throw new NotFoundException(MessageUtils.CONTRUCTION_NOT_FOUND);
    }

    // Xóa construction
    repository.deleteConstruction(constructionId);
    logger.info("Deleted construction record with id={}", constructionId);

    // Xử lý file nếu có
    if (construction.getFiles() != null && !construction.getFiles().isEmpty()) {
            // Xóa trên S3
            s3FileService.deleteFiles(construction.getFiles());
            logger.info("Deleted {} files from S3 for construction id={}", construction.getFiles().size(), constructionId);

            // Xóa trong DB
            List<String> uuids = construction.getFiles().stream()
                                        .map(FileDto::getUuId)
                                        .toList();
            fileRepository.deleteFilesByUuids(uuids);
            logger.info("Deleted {} files from DB for construction id={}", uuids.size(), constructionId);
    }

  }

  @Transactional
  @CacheEvict(value = "constructionList", allEntries = true)
  public ConstructionDto updateConstruction(ConstructionDto dto) {
      FileDiffResult diff = new FileDiffResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
      // Kiểm tra tồn tại
      if (!checkExistsConstruction(dto.getConstructionId())) {
        throw new NotFoundException(MessageUtils.CONTRUCTION_NOT_FOUND);
    }
      try {
          // Đồng bộ file với S3
          diff = fileSyncService.syncFiles(dto);
          
          // Update thông tin construction
          int updated = repository.updateConstruction(dto);
          if (updated <= 0) throw new NotFoundException(MessageUtils.UPDATE_FAIL);
          
          // Thêm file mới vào DB
          if (!diff.getToInsert().isEmpty()) {
              fileRepository.insertListFile(diff.getToInsert(), dto.getConstructionId());
          }
       // Xóa file cũ khỏi DB và S3 **ngoài transaction chính** để đảm bảo exception không bị wrap
          if (!diff.getToDelete().isEmpty()) {
              List<String> uuids = diff.getToDelete().stream().map(FileDto::getUuId).toList();
              // Xóa trong DB
              fileRepository.deleteFilesByUuids(uuids);
              // Xóa trên S3
              s3FileService.deleteFiles(diff.getToDelete());
          }
          // Trả về entity đã update
          return repository.getConstructionById(dto.getConstructionId());

      } catch (Exception e) {
          // Rollback file vừa upload
          s3FileService.deleteFiles(diff.getUploadedNow()); // rollback file mới
          throw new RuntimeException(MessageUtils.DELETE_FAIL , e);
      }
  }

  @Transactional
  public int deleteOldData() {
      LocalDateTime threshold = LocalDateTime.now().minusDays(1);
      return repository.deleteOlderThan(threshold);
  }

}
