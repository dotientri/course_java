// TẠO FILE MỚI: src/main/java/com/example/demo/dto/request/OrderPlacementRequest.java
package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderPlacementRequest {
    @NotNull(message = "Address ID không được để trống")
    private Long addressId;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    // Có thể thêm các trường khác trong tương lai như:
    // private String couponCode;
    // private String note;
}