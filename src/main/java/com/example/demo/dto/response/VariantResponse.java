// Sửa file: src/main/java/com/example/demo/dto/response/VariantResponse.java
package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class VariantResponse {
    private Long variantId;
    private String sku;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer stock;
    private List<String> images;

    // THAY ĐỔI: Trả về thuộc tính dưới dạng dễ đọc
    // Ví dụ: { "Màu sắc": "Đỏ", "Kích thước": "L" }
    private Map<String, String> attributes;
}