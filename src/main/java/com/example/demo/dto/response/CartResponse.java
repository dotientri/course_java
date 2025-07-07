package com.example.demo.dto.response;

// REMOVE this import: import com.example.demo.entity.CartItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    // CHANGE THIS LINE: Use the DTO, not the entity
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
}