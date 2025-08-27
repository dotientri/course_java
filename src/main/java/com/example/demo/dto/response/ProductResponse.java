// C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/dto/response/ProductResponse.java
package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String productName;
    private String description;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<VariantResponse> variants; // Thay thế các trường cũ
}