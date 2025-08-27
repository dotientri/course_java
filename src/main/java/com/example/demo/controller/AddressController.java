// Sửa file: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/controller/AddressController.java
package com.example.demo.controller;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ApiResponse<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.CREATED.value()) // Dùng 201 Created cho việc tạo mới
                .result(addressService.addAddress(request))
                .message("Address added successfully")
                .build();
    }

    @PutMapping("/{addressId}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(addressId, request))
                .message("Address updated successfully")
                .build();
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ApiResponse.<Void>builder()
                .message("Address deleted successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> getMyAddresses() {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getMyAddresses())
                .build();
    }

    @GetMapping("/{addressId}")
    public ApiResponse<AddressResponse> getAddress(@PathVariable Long addressId) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.getAddress(addressId))
                .build();
    }
}