// C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/dto/request/ProductCreationRequest.java
package com.example.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class ProductCreationRequest {
    @NotBlank @Size(min = 3, max = 200)
    String productName;

    @NotBlank
    String description;

    @NotNull
    Long categoryId;

    @NotEmpty(message = "Sản phẩm phải có ít nhất một biến thể.")
    @Valid // Quan trọng: để validate các object bên trong list
    List<VariantCreationRequest> variants;
}