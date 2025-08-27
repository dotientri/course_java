package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // === Lỗi Chung & Xác thực ===
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Mã khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1016, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1010, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),

    // === Lỗi liên quan đến User & Auth ===
    USER_EXISTED(1002, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1003, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1004, "Tên người dùng phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Mật khẩu phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1011, "Đăng nhập thất bại", HttpStatus.UNAUTHORIZED),
    EMAIL_NOT_VERIFIED(1015, "Email chưa được xác thực", HttpStatus.UNAUTHORIZED),
    INVALID_OTP(1017, "Mã OTP không hợp lệ", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(1013, "Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST),
    OTP_COOLDOWN(1014, "Vui lòng đợi một lát trước khi yêu cầu mã OTP mới", HttpStatus.BAD_REQUEST),

    // === Lỗi liên quan đến Product, Category, Attribute ===
    CATEGORY_EXISTED(2001, "Danh mục đã tồn tại", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(2002, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_EXISTED(2003, "Sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(2004, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    VARIANT_NOT_EXISTED(2005, "Biến thể sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    ATTRIBUTE_NOT_FOUND(2006, "Thuộc tính không tồn tại", HttpStatus.NOT_FOUND),
    ATTRIBUTE_VALUE_NOT_FOUND(2007, "Một hoặc nhiều giá trị thuộc tính không tồn tại", HttpStatus.NOT_FOUND),

    // === Lỗi liên quan đến Cart, Order, Stock ===
    CART_NOT_FOUND(3001, "Không tìm thấy giỏ hàng", HttpStatus.NOT_FOUND),
    CART_EMPTY(3002, "Giỏ hàng trống", HttpStatus.BAD_REQUEST),
    ITEM_NOT_IN_CART(3003, "Sản phẩm không có trong giỏ hàng", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(3004, "Số lượng không hợp lệ", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(3005, "Số lượng tồn kho không đủ", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(3006, "Đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(3007, "Địa chỉ không tồn tại", HttpStatus.NOT_FOUND),

    // === Lỗi liên quan đến Payment (PHẦN SỬA LỖI) ===
    INVALID_PAYMENT_METHOD(4001, "Phương thức thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT(4003, "Số tiền giao dịch không hợp lệ", HttpStatus.BAD_REQUEST), // FIX: Sửa lại message
    PAYMENT_NOT_FOUND(4002, "Không tìm thấy thông tin thanh toán", HttpStatus.NOT_FOUND);
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}