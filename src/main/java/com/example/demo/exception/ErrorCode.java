package com.example.demo.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"uncategorized exception"),
    INVALID_KEY(1001,"Invalid message key"),
    USER_EXISTED(1002,"User already existed"),
    USERNAME_INVALID(1003,"username must be at least 8 characters"),
    INVALID_PASSWORD(1004,"password  must be at least 8 characters"),
    USER_NOT_EXISTED(1005,"user not existed"),
    UNAUTHENTICATED(1006,"Unauthenticated !"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
