package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User already existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"username must be at least 8 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004,"password  must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"user not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Unauthenticated !", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"You do not have permission !", HttpStatus.FORBIDDEN),
    ;

    ErrorCode(int code, String message,HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
