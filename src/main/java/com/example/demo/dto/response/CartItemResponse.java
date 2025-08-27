// TẠO FILE MỚI: src/main/java/com/example/demo/dto/response/CartItemResponse.java
package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long id; // id của cart item
    private Long productId;
    private Long variantId;
    private String productName;
    private String color;
    private String size;
    private String imageUrl;
    private BigDecimal price; // Giá hiện tại của variant
    private int quantity;
    private int stock; // Tồn kho hiện tại của variant
}