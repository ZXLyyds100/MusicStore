package com.music.store.studioproject.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.store.studioproject.utils.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义认证入口点
 * <p>
 * 当一个未认证的用户（游客）尝试访问需要认证的受保护资源时，此处理器被调用。
 * 它会覆盖Spring Security默认的跳转到登录页的行为，而是返回一个统一的JSON响应。
 * </p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 使用BusinessException中定义的错误码，并自定义更友好的提示信息
        BusinessException be = BusinessException.unauthorized();
        Response<Object> errorResponse = Response.fail((int)be.getCode(), "访问需要认证，请登录后重试");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), errorResponse);
    }
}

