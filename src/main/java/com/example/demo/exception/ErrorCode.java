// src/main/java/com/example/demo/exception/ErrorCode.java
package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User already existed", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1015, "Email already existed", HttpStatus.BAD_REQUEST), // <-- Mã lỗi mới
    USERNAME_INVALID(1003,"username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004,"password  must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"user not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Unauthenticated !", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"You do not have permission !", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008,"You must be at least {min} years old", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(1009,"Token expired !", HttpStatus.UNAUTHORIZED),
    LOGIN_FAILED(1010,"Login failed !", HttpStatus.BAD_REQUEST),

    OTP_EXPIRED(1010, "OTP has expired.", HttpStatus.BAD_REQUEST),
    // THÊM MÃ LỖI MỚI VÀO ĐÂY
    OTP_COOLDOWN(1011, "Please wait a moment before requesting a new OTP.", HttpStatus.BAD_REQUEST),

    // Các mã lỗi mới cho xác thực và OTP
    EMAIL_NOT_VERIFIED(1011, "Email has not been verified", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1012, "Invalid verification token", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1013, "Invalid OTP", HttpStatus.BAD_REQUEST),

    // Các mã lỗi khác
    CART_EMPTY(2001, "Cart is empty", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND(2002, "Address not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(2003, "Order not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(2004, "Product not found", HttpStatus.NOT_FOUND),
    INVALID_PAYMENT_METHOD(2005, "Invalid payment method", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND(2006, "Payment not found", HttpStatus.NOT_FOUND),

    // GỢI Ý THÊM CÁC MÃ LỖI MỚI
    INSUFFICIENT_STOCK(2007, "Product is out of stock", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(2008, "Quantity must be at least 1", HttpStatus.BAD_REQUEST),
    COUPON_INVALID(2009, "Coupon is not valid or has expired", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1012, "Category does not exist.", HttpStatus.NOT_FOUND),

    // ADD THIS LINE
    PRODUCT_NOT_EXISTED(1013, "Product does not exist.", HttpStatus.NOT_FOUND),
    ITEM_NOT_IN_CART(1015, "The specified item is not in the cart", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1016, "Cart not found for the current user", HttpStatus.NOT_FOUND),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}