package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.ConstructionDto;
import com.example.demo.dto.request.ConstructionReigisterRequest;
import com.example.demo.dto.request.ContructionUpdateRequest;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.ConstructionService;
import com.example.demo.service.impl.ExportContructionService;
import com.example.demo.service.impl.ImportContructionService;
import com.example.demo.utils.CommonUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/construction")
public class ContructionController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private final ConstructionService constructionService;
  private final ExportContructionService exportService;
  private final ImportContructionService importService;

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> createContruction(
      @ModelAttribute @Valid ConstructionReigisterRequest request) {

    try {
      logger.info("LOGGING =>> Creating construction with files count: {}",
          request.getUploadFiles() != null ? request.getUploadFiles().size() : 0);

      CustomUserDetails userDetails = CommonUtils.getUserLogin();
      request.setUserID(userDetails.getUserId());
      request.setUserName(userDetails.getUsername());

      String constructionId = RandomStringUtils.randomAlphabetic(8);
      String userId = userDetails.getUserId();
      String username = userDetails.getUsername();
      ConstructionDto dto = ConstructionDto.fromRequest(request, constructionId, userId, username);
      ConstructionDto construction = constructionService.createConstruction(dto);

      return ResponseEntity.ok(construction);

    } catch (Exception e) {
      logger.error("Error creating construction: {}", e);
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Tạo construction thất bại: " + e));
    }
  }

  @GetMapping
  public ResponseEntity<List<ConstructionDto>> getAllUsers(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    List<ConstructionDto> users = constructionService.getListConstruction(page, size);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{constructionId}")
  public ResponseEntity<ConstructionDto> getContructionById(@PathVariable String constructionId) {
    logger.info("LOGGIN =>> start get detail user ->" + constructionId);
    return constructionService.getConstructionById(constructionId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/update")
  public ResponseEntity<ConstructionDto> updateContruction(
      @ModelAttribute @Valid ContructionUpdateRequest request) {

    logger.info("LOGGING Start UPDATE => convert request to entity -> " + request);

    // Lấy user login
    CustomUserDetails userDetails = CommonUtils.getUserLogin();
    String userId = userDetails.getUserId();
    String username = userDetails.getUsername();
    // constructionId lấy từ path, KHÔNG random mới
    ConstructionDto dto = ConstructionDto.fromRequest(request, userId, username);

    ConstructionDto updated = constructionService.updateConstruction(dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{constructionId}")
  public ResponseEntity<Void> deleteUser(@PathVariable String constructionId) {
    constructionService.deleteConstruction(constructionId);
    return ResponseEntity.noContent().build();
  }

  // ===== EXPORT =====
  @GetMapping("/export/csv")
  public void exportCsv(HttpServletResponse response) throws IOException {
    exportService.exportCsv(response);
  }

  @GetMapping("/export/excel")
  public void exportExcel(HttpServletResponse response) throws IOException {
    exportService.exportExcel(response);
  }

  // ===== IMPORT =====
  @PostMapping("/import/csv")
  public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
    try {
      importService.importCsv(file);
      return ResponseEntity.ok("Import CSV thành công!");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Import CSV thất bại: " + e.getMessage());
    }
  }

  @PostMapping("/import/excel")
  public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
    try {
      importService.importExcel(file);
      return ResponseEntity.ok("Import Excel thành công!");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Import Excel thất bại: " + e.getMessage());
    }
  }

}
