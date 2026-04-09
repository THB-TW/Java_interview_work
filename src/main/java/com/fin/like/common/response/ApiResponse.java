package com.fin.like.common.response;

public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;

    // 成功（帶資料）
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = "0000";
        response.message = "success";
        response.data = data;
        return response;
    }

    // 成功（無資料）
    public static <T> ApiResponse<T> success() {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = "0000";
        response.message = "success";
        response.data = null;
        return response;
    }

    // 失敗
    public static <T> ApiResponse<T> fail(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = code;
        response.message = message;
        response.data = null;
        return response;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
