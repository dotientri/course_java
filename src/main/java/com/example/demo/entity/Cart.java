package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // THIS IS THE MISSING PIECE:
    // This field, combined with the @Data annotation from Lombok,
    // automatically creates the getCartItems() method.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> cartItems;

    // HELPER METHOD: This is a clean way to calculate the total price.
    // Your OrderService is calling cart.getTotalPrice(), so this method is also required.
    public BigDecimal getTotalPrice() {
        if (cartItems == null || cartItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cartItems.stream()
                // Calculate price for each item (product price * quantity)
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                // Sum up all the item prices
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}