// src/main/java/com/example/demo/entity/Order.java
package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`order`") // Sử dụng backticks vì 'order' là từ khóa trong SQL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    User user;

    @ManyToOne
    @JsonBackReference("address-orders")
    Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    Payment payment;

    @Column(nullable = false)
    LocalDate orderDate; // **THÊM VÀO:** Ngày đặt hàng

    @Column(nullable = false)
    BigDecimal totalAmount; // **THÊM VÀO:** Tổng giá trị đơn hàng

    @Column(nullable = false)
    String paymentMethod; // **THÊM VÀO:** Phương thức thanh toán

    @Column(nullable = false)
    String status; // Trạng thái đơn hàng (PENDING, DELIVERED, CANCELED...)

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("order-items")
    private List<OrderItem> orderItems;

    // Các trường thời gian để theo dõi
    @Column(nullable = false, updatable = false) // Không cho phép cập nhật createdAt
            LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt; // **THÊM VÀO:** Thời gian cập nhật cuối cùng

    // Tự động gán giá trị thời gian trước khi lưu lần đầu
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Tự động cập nhật thời gian trước mỗi lần update
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}