package com.example.demo.controller;

import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.entity.Users;
import com.example.demo.request.RequestDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
  
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/create")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createUser(@RequestBody @Valid RequestDto request) {
        try {
            logger.info("LOGGIN =>> convert request to entity -> " + request);

            // Gọi service lưu user (giống register)
            Users savedUser = userService.saveUser(request);

            // Trả về user vừa tạo
            return ResponseEntity.ok(savedUser);

        } catch (IllegalArgumentException e) {
            // Bắt các lỗi do validate hoặc trùng userId/email
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Bắt các lỗi khác
            logger.error("Error creating user", e);
            return ResponseEntity.status(500).body("Lỗi hệ thống, tạo user thất bại");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size) {
      List<Users> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<Users> getUserByName(@PathVariable String userName) {
      logger.info("LOGGIN =>> start get detail user ->" + userName);
      Users user = userService.getUserByName(userName);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> updateUser(@RequestBody @Valid UpdateUserRequest request) {
      logger.info("LOGGIN Start UPDATE=>> convert request to entity ->" + request);
      
      Users user = Users.convertRequestToEntity(request);
      
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
