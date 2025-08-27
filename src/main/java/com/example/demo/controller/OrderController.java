// Sửa file: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/controller/OrderController.java
package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.request.OrderPlacementRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> placeOrder(@Valid @RequestBody OrderPlacementRequest request) {
        OrderResponse orderResponse = orderService.placeOrder(request.getAddressId(), request.getPaymentMethod());
        return ApiResponse.<OrderResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(orderResponse)
                .message("Order placed successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders())
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, status))
                .message("Order status updated")
                .build();
    }
}