package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long paymentId;
    private String paymentMethod;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
}