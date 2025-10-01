package com.music.store.studioproject.utils;

public class Response<T> {
    private int code;
    private String msg;
    private T data;

    public Response() {}

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // 成功响应
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }
    // 成功响应
    public static <T> Response<T> success(T data, String msg) {
        return new Response<>(200, msg, data);
    }

    public static <T> Response<T> success() {
        return new Response<>(200, "success", null);
    }

    // 失败响应
    public static <T> Response<T> fail(String msg) {
        return new Response<>(500, msg, null);
    }

    public static <T> Response<T> fail(int code, String msg) {
        return new Response<>(code, msg, null);
    }

    // 自定义响应
    public static <T> Response<T> custom(int code, String msg, T data) {
        return new Response<>(code, msg, data);
    }
}
