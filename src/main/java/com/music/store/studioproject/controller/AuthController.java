package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.LoginDto;
import com.music.store.studioproject.dto.LoginResponse;
import com.music.store.studioproject.dto.RefreshTokenDto;
import com.music.store.studioproject.entity.User;
import com.music.store.studioproject.exception.BusinessException;
import com.music.store.studioproject.service.UserService;
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
    private final UserService userService;
    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, RedisUtils redisUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.redisUtils = redisUtils;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody LoginDto loginDto) {
        // 1. 使用AuthenticationManager进行用户认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        // 2. 获取认证成功后的UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 使用 UserDetailsServiceImpl 的新方法加载自定义的 User 实体
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());


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
    public Response<LoginResponse> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String oldRefreshToken = refreshTokenDto.getRefreshToken();
        try {
            // 1. 解析并验证旧的RefreshToken
            Claims claims = jwtUtil.parseToken(oldRefreshToken);
            String jti = claims.get("jti", String.class);

            // 2. 验证Redis中的RefreshToken是否存在且匹配
            if (!redisUtils.isRefreshTokenValid(jti, oldRefreshToken)) {
                return Response.fail(401, "Refresh Token无效或已过期");
            }

            // 3. 从Redis中删除旧的RefreshToken，实现“一次性”使用
            redisUtils.deleteRefreshToken(jti);

            // 4. 从旧Token中提取用户信息，用于生成新Token
            Long userId = jwtUtil.getUserId(claims);
            String username = jwtUtil.getUsername(claims);
            String role = jwtUtil.getRole(claims);

            // 5. 生成新的AccessToken和RefreshToken
            String newAccessToken = jwtUtil.generateAccessToken(userId, username, role);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, username, role);

            // 6. 将新的RefreshToken存入Redis
            Claims newClaims = jwtUtil.parseToken(newRefreshToken);
            redisUtils.setRefreshToken(newClaims.get("jti", String.class), newRefreshToken);

            // 7. 返回新的Token对
            return Response.success(new LoginResponse(newAccessToken, newRefreshToken));
        } catch (Exception e) {
            // 统一处理Token解析、验证过程中的所有异常
            return Response.fail(401, "Refresh Token无效或已过期");
        }
    }
    @PostMapping("/register")
    public Response<LoginResponse> register(@RequestBody LoginDto loginDto) {
        User newUser = new User();
        newUser.setUsername(loginDto.getUsername());
        newUser.setPassword(loginDto.getPassword());
        newUser.setRoleId(2); // 默认分配普通用户角色

        // 这里可以添加更多的用户信息设置，比如邮箱、手机号等
        try {
            // 保存用户到数据库
            userService.saveUser(newUser);
        } catch (BusinessException e) {
            return Response.fail(400, e.getMessage());
        } catch (Exception e) {
            return Response.fail(500, "注册失败，请稍后重试");
        }
        // 注册成功后，直接登录并返回Token
        return login(loginDto);
    }
}
