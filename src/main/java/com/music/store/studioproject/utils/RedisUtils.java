package com.music.store.studioproject.utils;



import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类（存储Refresh Token，确保文档中登录态的安全性）
 */
@Component
public class RedisUtils {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final JwtUtil jwtUtil;

    // Redis键前缀（避免键冲突，格式：music:refresh:token:{jti}）
    private static final String REFRESH_TOKEN_KEY_PREFIX = "music:refresh:token:";

    public RedisUtils(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 存储Refresh Token到Redis（过期时间与Refresh Token一致）
     */
    public void setRefreshToken(String jti, String refreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + jti;
        // 转换过期时间为秒（Redis默认单位）
        long expireSeconds = jwtUtil.getRefreshTokenExpire() / 1000;
        redisTemplate.opsForValue().set(key, refreshToken, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 从Redis获取Refresh Token
     */
    public String getRefreshToken(String jti) {
        String key = REFRESH_TOKEN_KEY_PREFIX + jti;
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除Redis中的Refresh Token（登出/刷新Token时调用，使旧Token失效）
     */
    public void deleteRefreshToken(String jti) {
        String key = REFRESH_TOKEN_KEY_PREFIX + jti;
        redisTemplate.delete(key);
    }

    /**
     * 验证Refresh Token有效性（Redis中存在且与传入值一致，防止盗用）
     */
    public boolean isRefreshTokenValid(String jti, String refreshToken) {
        String storedToken = getRefreshToken(jti);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}