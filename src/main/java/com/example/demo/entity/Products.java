package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)  // Tự động tạo UUID
    String product_id;

    String product_name;

    @Column(columnDefinition = "TEXT")
    String product_description;

    @Column(precision = 10, scale = 2)
    BigDecimal product_price;

    @Column(columnDefinition = "JSON")
    String product_images;

    String product_category;
    String product_type;

    @Column(updatable = false)
    LocalDateTime created_at = LocalDateTime.now();

    @PrePersist
    void generateProductId() {
        if (product_name != null && !product_name.trim().isEmpty()) {
            this.product_id = product_name.toLowerCase().replaceAll("\\s+", "-");
        }
    }

}
