package com.example.demo.exception;

public class AppException extends RuntimeException {
    public AppException(ErrorCode errorCode) {
//        ke thua rumtiomeExeption
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public AppException(ErrorCode errorCode, String customMessage) {
        // Gọi constructor của lớp cha với message tùy chỉnh mà bạn truyền vào
        super(customMessage);
        this.errorCode = errorCode;
    }
    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
