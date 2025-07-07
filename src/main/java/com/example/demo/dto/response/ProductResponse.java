package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private List<String> colors;
    private List<String> sizes;
    private List<String> images; // Sẽ chứa các URL đến ảnh
    private String categoryName; // Chỉ hiển thị tên Category cho gọn
    private LocalDate createdAt;
}