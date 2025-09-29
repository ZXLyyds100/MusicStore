package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.LoginDto;
import com.music.store.studioproject.dto.LoginResponse;
import com.music.store.studioproject.entity.SysUser;
import com.music.store.studioproject.service.impl.UserDetailsServiceImpl;
import com.music.store.studioproject.utils.JwtUtil;
import com.music.store.studioproject.utils.RedisUtils;
import com.music.store.studioproject.utils.Response;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final RedisUtils redisUtils;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, RedisUtils redisUtils) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.redisUtils = redisUtils;
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody LoginDto loginDto) {
        // 1. 使用AuthenticationManager进行用户认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        // 2. 获取认证成功后的UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SysUser user = ((SysUser) userDetailsService.loadUserByUsername(userDetails.getUsername()));


        // 3. 获取用户角色
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String role = authorities.stream().findFirst().map(GrantedAuthority::getAuthority).orElse("");

        // 4. 生成AccessToken和RefreshToken
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername(), role);

        // 5. 将RefreshToken存入Redis
        Claims claims = jwtUtil.parseToken(refreshToken);
        redisUtils.setRefreshToken(claims.get("jti", String.class), refreshToken);

        // 6. 返回Token
        return Response.success(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public Response<String> refreshToken(@RequestBody String refreshToken) {
        try {
            // 1. 解析RefreshToken
            Claims claims = jwtUtil.parseToken(refreshToken);
            String jti = claims.get("jti", String.class);

            // 2. 验证Redis中的RefreshToken
            if (!redisUtils.isRefreshTokenValid(jti, refreshToken)) {
                return Response.fail(401, "Refresh Token无效或已过期");
            }

            // 3. 重新生成AccessToken
            Long userId = jwtUtil.getUserId(claims);
            String username = jwtUtil.getUsername(claims);
            String role = jwtUtil.getRole(claims);
            String newAccessToken = jwtUtil.generateAccessToken(userId, username, role);

            return Response.success(newAccessToken);
        } catch (Exception e) {
            return Response.fail(401, "Refresh Token无效或已过期");
        }
    }
}

