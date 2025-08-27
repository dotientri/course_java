// TẠO FILE MỚI: src/main/java/com/example/demo/dto/request/GoogleLoginRequest.java
package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank(message = "ID Token không được để trống")
    private String idToken;
}
    