// Sửa file: src/main/java/com/example/demo/dto/request/VariantCreationRequest.java
package com.example.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VariantCreationRequest {
    @NotBlank String sku;
    @NotNull @Positive BigDecimal price;
    @Positive BigDecimal salePrice;
    @NotNull @Min(0) Integer stock;
    List<String> images;

    // THAY ĐỔI: Nhận danh sách ID của các giá trị thuộc tính
    // Ví dụ: [1, 5] -> tương ứng với AttributeValue "Đỏ" và "Size L"
    @NotEmpty(message = "Một biến thể phải có ít nhất một thuộc tính.")
    List<Long> attributeValueIds;
}