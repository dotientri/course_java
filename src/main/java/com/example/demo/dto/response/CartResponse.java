// SỬA FILE: src/main/java/com/example/demo/dto/response/CartResponse.java
package com.example.demo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items; // THAY ĐỔI
    private BigDecimal totalPrice;
}