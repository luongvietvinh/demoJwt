package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.catalina.util.StringUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.ContructionDto;
import com.example.demo.dto.request.ContructionReigisterRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.entity.Users;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.ContructionService;
import com.example.demo.utils.CommonUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contruction")
public class ContructionController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private final ContructionService contructionService;

  public ContructionController(ContructionService contructionService) {
    this.contructionService = contructionService;
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> createContruction(
      @RequestBody @Valid ContructionReigisterRequest request) {
    logger.info("LOGGIN =>> convert request to dto -> " + request);
    CustomUserDetails userDetails = CommonUtils.getUserLogin();
    String contructionId = RandomStringUtils.randomAlphabetic(8);
    String userId = userDetails.getUserId();
    String username = userDetails.getUsername();
    ContructionDto dto = ContructionDto.fromRequest(request, contructionId, userId, username);

    ContructionDto contruction = contructionService.createContruction(dto);

    // Trả về contruction vừa tạo
    return ResponseEntity.ok(contruction);
  }

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> createContruction(
      @RequestPart("request") @Valid ContructionReigisterRequest request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {

    try {
      logger.info("LOGGING =>> Creating construction with files count: {}",
          files != null ? files.size() : 0);

      CustomUserDetails userDetails = CommonUtils.getUserLogin();
      request.setUserID(userDetails.getUserId());
      request.setUserName(userDetails.getUsername());

      // Set files vào request
      if (files != null && !files.isEmpty()) {
        request.setUploadFiles(files);
      }

      String contructionId = RandomStringUtils.randomAlphabetic(8);
      String userId = userDetails.getUserId();
      String username = userDetails.getUsername();
      ContructionDto dto = ContructionDto.fromRequest(request, contructionId, userId, username);
      ContructionDto construction = contructionService.createContruction(dto);

      return ResponseEntity.ok(construction);

    } catch (Exception e) {
      logger.error("Error creating construction: {}", e);
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Tạo construction thất bại: " + e));
    }
  }


  @GetMapping
  public ResponseEntity<List<ContructionDto>> getAllUsers(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    List<ContructionDto> users = contructionService.getListContruction(page, size);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{contructionId}")
  public ResponseEntity<ContructionDto> getContructionById(@PathVariable String contructionId) {
    logger.info("LOGGIN =>> start get detail user ->" + contructionId);
    return contructionService.getContructionById(contructionId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/update")
  public ResponseEntity<ContructionDto> updateUser(
      @RequestBody @Valid ContructionReigisterRequest request) {
    logger.info("LOGGIN Start UPDATE=>> convert request to entity ->" + request);
    CustomUserDetails userDetails = CommonUtils.getUserLogin();
    String contructionId = RandomStringUtils.randomAlphabetic(8);
    String userId = userDetails.getUserId();
    String username = userDetails.getUsername();
    ContructionDto dto = ContructionDto.fromRequest(request, contructionId, userId, username);
    return ResponseEntity.ok(contructionService.updateContruction(dto));
  }

  @DeleteMapping("/{contructionId}")
  public ResponseEntity<Void> deleteUser(@PathVariable String contructionId) {
    contructionService.deleteContruction(contructionId);
    return ResponseEntity.noContent().build();
  }
}
