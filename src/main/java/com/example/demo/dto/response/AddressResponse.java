// TẠO FILE MỚI: src/main/java/com/example/demo/dto/response/AddressResponse.java
package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String fullAddress; // Một trường tiện ích để hiển thị địa chỉ đầy đủ
    private boolean isDefault;
}