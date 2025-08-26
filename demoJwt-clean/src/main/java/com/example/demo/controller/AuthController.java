package com.example.demo.controller;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RefreshRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserInfo;
import com.example.demo.entity.Users;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtTokenUtil jwt;
  private final UserService userService;
  private final CustomUserDetailsService userDetailsService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    try {
      Users u = userService.register(req);
      return ResponseEntity.ok("Đăng ký thành công: " + u);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
    Authentication authentication = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUserName(), req.getPassWord()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    CustomUserDetails cud = (CustomUserDetails) authentication.getPrincipal();
    // Tạo token
    String accessToken = jwt.generateAccessToken(cud.getUserId(), cud.getUsername(), null);

    // Lấy UserDetails lại để đọc roles (ở đây roles nằm trong authorities)
    Set<String> roles = cud.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority) // trả về "ROLE_USER", "ROLE_ADMIN", ...
        .map(r -> r) // bỏ prefix "ROLE_"
        .collect(Collectors.toSet());

    accessToken = jwt.generateAccessToken(cud.getUserId(), cud.getUsername(), roles);
    String refreshToken = jwt.generateRefreshToken(cud.getUserId(), cud.getUsername());

    // Trả user info
    UserInfo info = UserInfo.builder()
        .userId(cud.getUserId())
        .username(cud.getUsername())
        .roles(roles)
        .build();

    return ResponseEntity.ok(AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwt.getAccessExpMs())
        .user(info)
        .build());
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) {
    // Validate refresh token
    if (!jwt.validate(req.getRefreshToken())) {
      return ResponseEntity.status(401).build();
    }
    var claims = jwt.parse(req.getRefreshToken()).getBody();
    if (!"refresh".equals(claims.get("type"))) {
      return ResponseEntity.status(401).build();
    }

    String username = claims.getSubject();
    String userId = ((String) claims.get("uid"));

    // Load user để lấy roles hiện tạ
    var userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
    Set<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority) // trả về "ROLE_USER", "ROLE_ADMIN", ...
        .map(r -> r) // bỏ prefix "ROLE_"
        .collect(Collectors.toSet());


    // Cấp access token mới (refresh vẫn giữ nguyên do client giữ)
    String newAccess = jwt.generateAccessToken(userId, username, roles);

    UserInfo info = UserInfo.builder()
        .userId(userId)
        .username(username)
        .roles(roles)
        .build();

    return ResponseEntity.ok(AuthResponse.builder()
        .accessToken(newAccess)
        .refreshToken(req.getRefreshToken())
        .tokenType("Bearer")
        .expiresIn(jwt.getAccessExpMs())
        .user(info)
        .build());
  }
}
