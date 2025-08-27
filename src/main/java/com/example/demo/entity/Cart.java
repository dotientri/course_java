package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    // EAGER fetch is often useful for carts, as you usually need the items when you load the cart.
    // @Builder.Default ensures the list is initialized.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    List<CartItem> cartItems = new ArrayList<>();

    // **THÊM TRƯỜNG NÀY ĐỂ SỬA LỖI**
    // Lưu tổng giá trị của giỏ hàng, được tính toán bởi CartService.
    @Column(precision = 10, scale = 2)
    BigDecimal totalPrice;
}