package com.example.demo.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCreationRequest {
    private String productName;
    private String description;
    private BigDecimal price;
    private List<String> colors;
    private List<String> sizes;
    private Long categoryId; // Chỉ cần ID của category khi tạo
}