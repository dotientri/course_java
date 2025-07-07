// src/main/java/com/example/demo/entity/OrderItem.java
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
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JsonBackReference("order-items")
    Order order;

    @ManyToOne(optional = false)
    Product product;

    @Column(nullable = false)
    Integer quantity;

    @Column(nullable = false)
    Double priceAtOrder;

    private BigDecimal price; // Giá của sản phẩm tại thời điểm đặt hàng

}