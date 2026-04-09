package com.fin.like.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    public BusinessException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    // 常用：找不到資源
    public static BusinessException notFound(String message) {
        return new BusinessException("4040", message, HttpStatus.NOT_FOUND);
    }

    // 常用：請求參數錯誤
    public static BusinessException badRequest(String message) {
        return new BusinessException("4000", message, HttpStatus.BAD_REQUEST);
    }

    // 常用：系統錯誤
    public static BusinessException internalError(String message) {
        return new BusinessException("5000", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String getCode() { return code; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
