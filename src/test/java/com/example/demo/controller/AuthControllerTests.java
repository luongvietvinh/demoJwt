package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Thêm import này
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import com.amazonaws.services.s3.AmazonS3; // Thêm import này
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.request.AuthRequest;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTests {
  
    private AuthenticationManager authManager;
    private JwtTokenUtil jwt;
    private UserService userService;
    private CustomUserDetailsService userDetailsService;
    private AuthController controller;
    
    @MockBean
    private AmazonS3 amazonS3;

    @BeforeEach
    public void setup() {
        authManager = mock(AuthenticationManager.class);
        jwt = mock(JwtTokenUtil.class);
        userService = mock(UserService.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        controller = new AuthController(authManager, jwt, userDetailsService);
    }

    @SuppressWarnings("deprecation")
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