package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.entity.Address;
import com.example.demo.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ApiResponse<Address> addAddress(@RequestBody Address address) {
        return ApiResponse.<Address>builder()
                .result(addressService.addAddress(address))
                .message("Address added")
                .build();
    }

    @PutMapping("/{addressId}")
    public ApiResponse<Address> updateAddress(@PathVariable Long addressId, @RequestBody Address address) {
        return ApiResponse.<Address>builder()
                .result(addressService.updateAddress(addressId, address))
                .message("Address updated")
                .build();
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ApiResponse.<Void>builder()
                .message("Address deleted")
                .build();
    }

    @GetMapping
    public ApiResponse<List<Address>> getMyAddresses() {
        return ApiResponse.<List<Address>>builder()
                .result(addressService.getMyAddresses())
                .build();
    }

    @GetMapping("/{addressId}")
    public ApiResponse<Address> getAddress(@PathVariable Long addressId) {
        return ApiResponse.<Address>builder()
                .result(addressService.getAddress(addressId))
                .build();
    }
}