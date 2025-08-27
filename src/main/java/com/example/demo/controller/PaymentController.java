// C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/controller/PaymentController.java
package com.example.demo.controller;

import com.example.demo.configuration.VnPayProperties;
import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.response.VnPayIpnResponse;
import com.example.demo.entity.Order;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final VnPayService vnPayService;
    private final OrderRepository orderRepository;
    private final VnPayProperties vnPayProperties;

    @PostMapping("/create-vnpay/{orderId}")
    public ApiResponse<String> createVnPayPayment(
            @PathVariable Long orderId,
            HttpServletRequest request) throws UnsupportedEncodingException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // FIX: Convert BigDecimal to double before rounding
        long amount = Math.round(order.getTotalAmount().doubleValue());
        String orderInfo = "Thanh toan don hang " + order.getId();
        String paymentUrl = vnPayService.createPaymentUrl(order.getId(), amount, orderInfo, request);

        return ApiResponse.<String>builder()
                .result(paymentUrl)
                .message("Payment URL created successfully.")
                .build();
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> vnPayReturn(HttpServletRequest request) {
        String finalUrl = vnPayProperties.getFrontendReturnUrl() + "?" + request.getQueryString();
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", finalUrl).build();
    }

    /**
     * Endpoint VNPay gọi về server-to-server (IPN)
     * Đã được đơn giản hóa, toàn bộ logic nằm trong service.
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<VnPayIpnResponse> vnPayIpn(HttpServletRequest request) {
        VnPayIpnResponse response = vnPayService.handleIpn(request);
        return ResponseEntity.ok(response);
    }
}