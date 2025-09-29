package com.music.store.studioproject.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类（生成Access/Refresh Token，支撑文档中三类角色的登录认证）
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:music-store-secret}")
    private String jwtSecret;

    // Access Token过期时间：15分钟（短期，降低被盗用风险）
    @Value("${jwt.access-expire:900000}")
    private long accessTokenExpire;

    // Refresh Token过期时间：7天（长期，用于刷新Access Token）
    @Value("${jwt.refresh-expire:604800000}")
    private long refreshTokenExpire;

    // 生成用于签名与验签的密钥（HS256 要求至少 256-bit，即 32 字节长度）
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Token（通用方法，含用户ID、用户名、角色，贴合文档角色权限）
     */
    private String generateToken(Long userId, String username, String role, long expire) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);       // 用户ID（关联数据库用户）
        claims.put("username", username);   // 用户名（登录标识）
        claims.put("role", role);           // 角色（ROLE_ADMIN/ROLE_USER/ROLE_GUEST，对应文档三类角色）
        claims.put("jti", UUID.randomUUID().toString()); // 唯一标识（关联Redis中的Refresh Token）

        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + expire);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成Access Token（短期，用于接口访问）
     */
    public String generateAccessToken(Long userId, String username, String role) {
        return generateToken(userId, username, role, accessTokenExpire);
    }

    /**
     * 生成Refresh Token（长期，用于刷新Access Token）
     */
    public String generateRefreshToken(Long userId, String username, String role) {
        return generateToken(userId, username, role, refreshTokenExpire);
    }

    /**
     * 解析Token，获取载荷信息
     * @throws ExpiredJwtException Token过期
     * @throws MalformedJwtException Token格式错误
     * @throws SignatureException 签名失败
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token已过期");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Token格式错误");
        } catch (SignatureException e) {
            throw new SignatureException("Token签名验证失败");
        } catch (Exception e) {
            throw new IllegalArgumentException("无效Token");
        }
    }

    // 从载荷中提取信息的工具方法
    public Long getUserId(Claims claims) {
        return claims.get("userId", Long.class);
    }

    public String getUsername(Claims claims) {
        return claims.get("username", String.class);
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public String getJti(Claims claims) {
        return claims.get("jti", String.class);
    }

    // Getter（给其他类提供过期时间）
    public long getAccessTokenExpire() {
        return accessTokenExpire;
    }

    public long getRefreshTokenExpire() {
        return refreshTokenExpire;
    }
}