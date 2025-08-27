package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonBackReference("order-items")
    Order order;

    // THAY ĐỔI: Liên kết trực tiếp đến một biến thể sản phẩm cụ thể
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    ProductVariant variant;

    @Column(nullable = false)
    Integer quantity;

    // THAY ĐỔI: Lưu giá của biến thể tại thời điểm đặt hàng để đảm bảo tính toàn vẹn dữ liệu
    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal priceAtOrder;
}