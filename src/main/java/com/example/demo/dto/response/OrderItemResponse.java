package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price; // Giá của sản phẩm tại thời điểm thêm vào giỏ/đặt hàng
    private String imageUrl; // URL ảnh đại diện của sản phẩm
}