package com.example.demo.service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserMapper;
import com.example.demo.request.RequestDto;
import com.example.demo.service.mail.EmailService;

@Service
public class UserService implements IuserService {

  private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  @Autowired
  private final UserMapper repository;

  @Autowired
  private EmailService emailService;
  @Autowired
  private final BCryptPasswordEncoder encoder;

  public UserService(UserMapper repository, BCryptPasswordEncoder encoder) {
    this.repository = repository;
    this.encoder = encoder;
  }

  @Override
  @Transactional
  public Users saveUser(RequestDto request) {
    if (repository.existsByUserName(request.getUserName())) {
      throw new IllegalArgumentException("Username đã tồn tại");
    }
    Users user = Users.builder()
        .userId(generateUserId())
        .userName(request.getUserName())
        .passWord(encoder.encode(request.getPassWord()))
        .mail(request.getMail())
        .isEnabled(true)
        .roles(request.getRoles())
        .build();
    repository.insert(user);
    logger.info("LOGGIN =>> create user done -> OK");

    List<RoleEntity> roleEntities = request.getRoles().stream()
        .map(role -> RoleEntity.builder()
            .userId(user.getUserId())
            .roleCode(role)
            .isActive(true)
            .build())
        .collect(Collectors.toList());

    repository.insertRole(roleEntities);

    // GỌI GỬI EMAIL SAU KHI MỌI THỨ ĐÃ LƯU THÀNH CÔNG
    // Gửi mật khẩu gốc (chưa mã hóa) từ request
    try {
      emailService.sendRegistrationSuccessEmail(
          user.getMail(),
          user.getUserName(),
          request.getPassWord() // Lấy mật khẩu gốc từ DTO

      );
      logger.info("LOGGIN =>> SEND MAIL success");
    } catch (Exception e) {
      // Dù gửi mail thất bại, cũng không nên làm hỏng transaction đăng ký
      // Chỉ cần log lại lỗi để kiểm tra sau
      logger.error("Không thể gửi email chào mừng cho user: {}", user.getUserName(), e);
    }
    return user;
  }

  @Override
  public List<Users> getAllUsers(int page, int size) {
    if (page == 0) {
      page++;
    }
    int offset = (page - 1) * size;
    List<Users> users = repository.findAll(size, offset);
    users.stream().map(Users::getEnumRoles).collect(Collectors.toList());

    return users;
  }

  public Optional<Users> getUserByName(String userName) {
    return Optional.ofNullable(repository.findByName(userName))
        .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
  }

  @Override
  public void deleteUser(String id) {
    repository.delete(id);
  }

  @Override
  public Users updateUser(Users user) {
    Optional.ofNullable(repository.findByName(user.getUserName()))
        .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
    repository.update(user);
    logger.info("LOGGIN =>> Update user" + user.getUserName() + " done -> OK");
    return user;
  }


  @Transactional
  public Users register(RegisterRequest req) {
    if (repository.existsByUserName(req.getUserName())) {
      throw new IllegalArgumentException("Username đã tồn tại");
    }


    Set<String> roles = (req.getRoles() == null || req.getRoles().isEmpty())
        ? Collections.emptySet()
        : req.getRoles();

    // settuser
    Users user = Users.builder()
        .userId(generateUserId())
        .userName(req.getUserName())
        .passWord(encoder.encode(req.getPassWord()))
        .mail(null)
        .isEnabled(true)
        .roles(roles)
        .build();
    List<RoleEntity> roleEntities = req.getRoles().stream()
        .map(role -> RoleEntity.builder()
            .userId(user.getUserId())
            .roleCode(role)
            .isActive(true)
            .build())
        .collect(Collectors.toList());
    repository.insert(user);
    repository.insertRole(roleEntities);
    return user;
  }

  private String generateUserId() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
    }
    return sb.toString();
  }
}
