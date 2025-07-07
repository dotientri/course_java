package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/add")
    public ApiResponse<Payment> addPayment(@RequestParam Long orderId, @RequestParam String method) {
        return ApiResponse.<Payment>builder()
                .result(paymentService.addPayment(orderId, method))
                .message("Payment info added")
                .build();
    }

    @PutMapping("/{paymentId}/status")
    public ApiResponse<Payment> updatePaymentStatus(@PathVariable Long paymentId, @RequestParam String status) {
        return ApiResponse.<Payment>builder()
                .result(paymentService.updatePaymentStatus(paymentId, status))
                .message("Payment status updated")
                .build();
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<Payment> getPayment(@PathVariable Long paymentId) {
        return ApiResponse.<Payment>builder()
                .result(paymentService.getPayment(paymentId))
                .build();
    }
}