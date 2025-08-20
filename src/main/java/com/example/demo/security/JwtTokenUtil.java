package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import com.example.demo.Role;
import com.example.demo.entity.RoleEntity;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

// Quản lý tạo/đọc/validate Access & Refresh tokens
@Component
public class JwtTokenUtil {

    // ⚠️ Đưa các giá trị này vào application.properties ở thực tế
    private final String SECRET = "change_this_super_long_secret_key_for_jwt_256_bits_here_1234567890";
    private final long ACCESS_EXP_MS  = 15 * 60 * 1000L;   // 15 phút
    private final long REFRESH_EXP_MS = 7L * 24 * 60 * 60 * 1000L; // 7 ngày

    private Key key() { return Keys.hmacShaKeyFor(SECRET.getBytes()); }

    // Tạo Access Token: chứa thông tin tối cần thiết
    public String generateAccessToken(String userId, String username, Collection<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ACCESS_EXP_MS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("uname", username);
        claims.put("roles", roles == null? List.of() : roles.stream().collect(Collectors.toList()));
        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Tạo Refresh Token: cũng chứa 1 số claim để tiện kiểm tra
    public String generateRefreshToken(String userId, String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + REFRESH_EXP_MS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("type", "refresh");

        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public String getUserId(String token) {
        Object uid = parse(token).getBody().get("uid");
        return uid == null ? null : String.valueOf(uid.toString());
    }

    public List<String> getRoles(String token) {
        Object r = parse(token).getBody().get("roles");
        if (r instanceof List<?> list) return list.stream().map(String::valueOf).collect(Collectors.toList());
        return List.of();
    }

    public long getAccessExpMs() { return ACCESS_EXP_MS; }
}
