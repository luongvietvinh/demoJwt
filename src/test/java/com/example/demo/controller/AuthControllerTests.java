package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Users;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.UserService;

@SpringBootTest
public class AuthControllerTests {
    private AuthenticationManager authManager;
    private JwtTokenUtil jwt;
    private UserService userService;
    private CustomUserDetailsService userDetailsService;
    private AuthController controller;

    @BeforeEach
    public void setup() {
        authManager = mock(AuthenticationManager.class);
        jwt = mock(JwtTokenUtil.class);
        userService = mock(UserService.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        controller = new AuthController(authManager, jwt, userService, userDetailsService);
    }

    @Test
    void testSomething() {
        System.out.println("Running test...");
    }
    // ✅ Test cho phương thức register
    @Test
    public void testRegisterSuccess() {
       Set<String> roles = Set.of("1", "2");
        RegisterRequest req = new RegisterRequest("user", "pass", roles);
        Users mockUser = new Users(); // giả định có class Users
        when(userService.register(req)).thenReturn(mockUser);

        ResponseEntity<?> response = controller.register(req);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Đăng ký thành công"));
    }

    @Test
    public void testRegisterFail() {
        Set<String> roles = Set.of("1", "2");
        RegisterRequest req = new RegisterRequest("user", "pass", roles);
        when(userService.register(req)).thenThrow(new IllegalArgumentException("Tài khoản đã tồn tại"));

        ResponseEntity<?> response = controller.register(req);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Tài khoản đã tồn tại", response.getBody());
    }

    // ✅ Test cho phương thức login
    @Test
    public void testLoginSuccess() {
        AuthRequest req = new AuthRequest("user", "pass");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn("123");
        when(userDetails.getUsername()).thenReturn("user");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Mockito.<Collection<? extends GrantedAuthority>>when(userDetails.getAuthorities()).thenReturn(authorities);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authManager.authenticate(any())).thenReturn(authentication);

        when(jwt.generateAccessToken(anyString(), anyString(), any())).thenReturn("access-token");
        when(jwt.generateRefreshToken(anyString(), anyString())).thenReturn("refresh-token");
        when(jwt.getAccessExpMs()).thenReturn(3600000L);

        ResponseEntity<AuthResponse> response = controller.login(req);

        assertEquals(200, response.getStatusCodeValue());
        AuthResponse auth = response.getBody();
        assertNotNull(auth);
        assertEquals("access-token", auth.getAccessToken());
        assertEquals("refresh-token", auth.getRefreshToken());
        assertEquals("Bearer", auth.getTokenType());
        assertEquals(3600000L, auth.getExpiresIn());
        assertEquals("user", auth.getUser().getUsername());
        assertEquals(Set.of("ROLE_USER"), auth.getUser().getRoles());
    }

}
