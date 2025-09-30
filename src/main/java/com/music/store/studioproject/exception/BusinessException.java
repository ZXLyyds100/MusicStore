package com.music.store.studioproject.exception;

import lombok.Getter;

/**
 * 自定义业务异常统一处理类
 * <p>
 * 该类整合了错误码枚举、业务异常以及用于创建和匹配异常的工厂方法。
 * 通过这种方式，所有业务异常相关的定义都集中在一个文件中，方便管理。
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {

    // ------------------- 1. 错误码枚举中心 -------------------
    /**
     * 业务错误码枚举
     * <p>
     * 定义了系统中所有已知的业务错误。
     * </p>
     */
    @Getter
    private enum ErrorCode {

        // ========== 系统级别错误码 (10000-10999) ==========
        SYSTEM_ERROR(10001, "系统繁忙，请稍后重试"),
        INVALID_PARAMETER(10002, "参数错误"),

        // ========== 用户相关错误码 (11000-11999) ==========
        USER_NOT_FOUND(11001, "用户不存在"),
        USER_PASSWORD_ERROR(11002, "用户名或密码错误"),
        USER_ACCOUNT_DISABLED(11003, "账户已被禁用"),
        USER_ALREADY_EXISTS(11004, "用户已存在"),

        // ========== 认证授权相关错误码 (12000-12999) ==========
        UNAUTHORIZED(12001, "未经授权的访问"),
        TOKEN_EXPIRED(12002, "Token已过期"),
        TOKEN_INVALID(12003, "无效的Token"),
        INSUFFICIENT_PERMISSIONS(12004, "权限不足"),

        // ========== 其他业务错误码 (根据业务模块划分) ==========
        RESOURCE_NOT_FOUND(13001, "请求的资源不存在");

        private final long code;
        private final String message;

        ErrorCode(long code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    // ------------------- 2. 异常类核心实现 -------------------
    private final ErrorCode errorCode;
    @Getter
    private String externalErrorDetails;

    /**
     * 构造函数
     * @param errorCode 错误码枚举
     */
    private BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * @param errorCode 错误码枚举
     * @param externalErrorDetails 外部错误详情
     */
    private BusinessException(ErrorCode errorCode, String externalErrorDetails) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.externalErrorDetails = externalErrorDetails;
    }

    /**
     * 构造函数
     * @param errorCode 错误码枚举
     * @param externalErrorDetails 外部错误详情
     * @param cause     原始异常
     */
    private BusinessException(ErrorCode errorCode, String externalErrorDetails, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.externalErrorDetails = externalErrorDetails;
    }

    // ------------------- 3. 静态工厂方法 -------------------

    public static BusinessException systemError(String details) {
        return new BusinessException(ErrorCode.SYSTEM_ERROR, details);
    }

    public static BusinessException invalidParameter(String details) {
        return new BusinessException(ErrorCode.INVALID_PARAMETER, details);
    }

    public static BusinessException userNotFound(String details) {
        return new BusinessException(ErrorCode.USER_NOT_FOUND, details);
    }

    public static BusinessException userPasswordError() {
        return new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
    }

    public static BusinessException userAccountDisabled(String details) {
        return new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED, details);
    }

    public static BusinessException userAlreadyExists(String details) {
        return new BusinessException(ErrorCode.USER_ALREADY_EXISTS, details);
    }

    public static BusinessException unauthorized() {
        return new BusinessException(ErrorCode.UNAUTHORIZED);
    }

    public static BusinessException tokenExpired() {
        return new BusinessException(ErrorCode.TOKEN_EXPIRED);
    }

    public static BusinessException tokenInvalid() {
        return new BusinessException(ErrorCode.TOKEN_INVALID);
    }

    public static BusinessException insufficientPermissions() {
        return new BusinessException(ErrorCode.INSUFFICIENT_PERMISSIONS);
    }

    public static BusinessException resourceNotFound(String details) {
        return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, details);
    }

    // ------------------- 4. 错误码匹配方法 -------------------

    public static boolean isSystemError(BusinessException e) {
        return e.getErrorCode() == ErrorCode.SYSTEM_ERROR;
    }

    public static boolean isInvalidParameter(BusinessException e) {
        return e.getErrorCode() == ErrorCode.INVALID_PARAMETER;
    }

    public static boolean isUserNotFound(BusinessException e) {
        return e.getErrorCode() == ErrorCode.USER_NOT_FOUND;
    }

    public static boolean isUserPasswordError(BusinessException e) {
        return e.getErrorCode() == ErrorCode.USER_PASSWORD_ERROR;
    }

    public static boolean isUserAccountDisabled(BusinessException e) {
        return e.getErrorCode() == ErrorCode.USER_ACCOUNT_DISABLED;
    }

    public static boolean isUserAlreadyExists(BusinessException e) {
        return e.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS;
    }

    public static boolean isUnauthorized(BusinessException e) {
        return e.getErrorCode() == ErrorCode.UNAUTHORIZED;
    }

    public static boolean isTokenExpired(BusinessException e) {
        return e.getErrorCode() == ErrorCode.TOKEN_EXPIRED;
    }

    public static boolean isTokenInvalid(BusinessException e) {
        return e.getErrorCode() == ErrorCode.TOKEN_INVALID;
    }

    public static boolean isInsufficientPermissions(BusinessException e) {
        return e.getErrorCode() == ErrorCode.INSUFFICIENT_PERMISSIONS;
    }

    public static boolean isResourceNotFound(BusinessException e) {
        return e.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND;
    }

    // ------------------- 5. 重写方法 -------------------

    /**
     * 直接获取错误码的long值
     * @return 错误码
     */
    public long getCode() {
        return this.errorCode.getCode();
    }

    @Override
    public String getMessage() {
        String baseMessage = String.format("Error Code: %d, Error Info: %s",
                errorCode.getCode(), errorCode.getMessage());
        if (externalErrorDetails != null) {
            return String.format("%s, External Error Details: %s", baseMessage, externalErrorDetails);
        }
        return baseMessage;
    }
}
