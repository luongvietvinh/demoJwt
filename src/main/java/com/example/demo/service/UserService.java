package com.example.demo.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.Users;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.UserMapper;
import com.example.demo.request.RequestDto;
import com.example.demo.service.mail.EmailService;
import com.example.demo.utils.MessageUtils;

@Service
public class UserService implements IuserService {

  private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  
  private final UserMapper repository;
  private final EmailService emailService;
  private final BCryptPasswordEncoder encoder;

  public UserService(UserMapper repository, EmailService emailService) {
    this.repository = repository;
    this.emailService = emailService;
    this.encoder = new BCryptPasswordEncoder();
  }

  @Override
  @Transactional
  public Users saveUser(RequestDto request) {
    if (repository.existsByUserName(request.getUserName())) {
      throw new NotFoundException(MessageUtils.USER_EXITED);
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
            .userName(user.getUserName())
            .roleCode(role)
            .isActive(true)
            .build())
        .collect(Collectors.toList());

    repository.insertRole(roleEntities);

    // GỌI GỬI EMAIL SAU KHI MỌI THỨ ĐÃ LƯU THÀNH CÔNG
    // Gửi mật khẩu gốc (chưa mã hóa) từ request
    try {
      Map<String,Object> variables = new HashMap<>();
      String subject = "Đăng ký user thành công";
      variables.put("userName", user.getUserName());
      variables.put("passWord", request.getPassWord());
      variables.put("subject", subject);
      
      String template = "MailTemplate";
      
      emailService.sendEmail(user.getMail(), subject,
          template,variables );
      
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

  @Override
  public Users getUserByName(String userName) {
    Users user = repository.findByName(userName);
    if (user == null) {
        throw new NotFoundException(MessageUtils.USER_NOT_FOUND);
    }
    return user;
  }

  @Override
  public void deleteUser(String userId) {
    repository.delete(userId);
    repository.deleteRoleByUserId(userId);
  }

  @Override
  public Users updateUser(Users user) {
    Optional.ofNullable(repository.findByUserId(user.getUserId())
        .orElseThrow(() -> new NotFoundException(MessageUtils.USER_NOT_FOUND)));
    repository.update(user);
    logger.info("=>>> UPDATE user" + user.getUserName() + " done -> OK");
    // check update /delete role
    if(user.getRoles()!= null && !user.getRoles().isEmpty()) {
      repository.deleteRoleByUserId(user.getUserId());
    }
    List<RoleEntity> roleEntities = user.getRoles().stream()
        .map(role -> RoleEntity.builder()
            .userId(user.getUserId())
            .userName(user.getUserName())
            .roleCode(role)
            .isActive(true)
            .build())
        .collect(Collectors.toList());

    repository.insertRole(roleEntities);
    logger.info("INSERT ROLE =>> Update user" + user.getUserName() + " done -> OK");
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
