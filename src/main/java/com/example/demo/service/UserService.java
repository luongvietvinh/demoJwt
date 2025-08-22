package com.example.demo.service;

import com.example.demo.Role;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.Users;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.UserMapper;
import com.example.demo.request.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IuserService {
  
  private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserMapper repository;
    
    private final BCryptPasswordEncoder encoder;

    public UserService(UserMapper repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
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
      RoleEntity role = RoleEntity.builder()
          .userId(user.getUserId())
          .role(Role.USER.getValue())
          .isActive(true)
          .build();
      repository.insertRole(role);
        return user;
    }

    @Override
    public List<Users> getAllUsers(int page, int size) {
      if(page == 0) {
        page++;
      }
      int offset = (page - 1) * size;
        return repository.findAll(size, offset );
    }

    @Override
    public Optional<Users> getUserById(String id) {
        return Optional.ofNullable(repository.findById(id));
    }

    @Override
    public void deleteUser(String id) {
      repository.delete(id);
    }

    @Override
    public Users updateUser(Users user) {
      List<String> errors = new ArrayList<>();
      Users exitUsser = repository.findById(user.getUserId());
      if(exitUsser == null) {
        errors.add(" user chưa tồn tại!");
      }
      if (!errors.isEmpty()) {
        throw new ValidationException(errors);
    }
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
          ? Collections.emptySet() : req.getRoles(); 

      // settuser
      Users user = Users.builder()
              .userId(generateUserId())
              .userName(req.getUserName())
              .passWord(encoder.encode(req.getPassWord()))
              .mail(null)
              .isEnabled(true)
              .roles(roles)
              .build();
      RoleEntity role = RoleEntity.builder()
          .userId(user.getUserId())
          .role(Role.USER.getValue())
          .isActive(true)
          .build();
      repository.insert(user);
      repository.insertRole(role);
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
