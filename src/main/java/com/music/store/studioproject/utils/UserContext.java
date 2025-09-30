package com.music.store.studioproject.utils;

/**
 * 使用ThreadLocal存储当前登录用户信息的上下文工具类
 * <p>
 * 方便在业务代码的任何地方获取当前用户信息，而无需通过方法层层传递。
 * </p>
 */
public class UserContext {

    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> roleHolder = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }

    public static Long getUserId() {
        return userIdHolder.get();
    }

    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    public static String getUsername() {
        return usernameHolder.get();
    }

    public static void setRole(String role) {
        roleHolder.set(role);
    }

    public static String getRole() {
        return roleHolder.get();
    }

    /**
     * 清除所有存储在ThreadLocal中的信息
     * <p>
     * 必须在请求处理完成后（如在Filter的finally块中）调用，以防内存泄漏。
     * </p>
     */
    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
        roleHolder.remove();
    }
}

