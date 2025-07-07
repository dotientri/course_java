// src/main/java/com/example/demo/entity/Product.java
package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productId;

    @Column(nullable = false, unique = true)
    String productName;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    Double originalPrice;

    Double salePrice;

    @ElementCollection
    List<String> colors;

    @ElementCollection
    List<String> sizes;

    @ElementCollection
    List<String> images;

    @ManyToOne(optional = false)
    @JsonBackReference("category-product")
    Category category;

    @Column(nullable = false, updatable = false)
    LocalDate createdAt;

    LocalDateTime updatedAt;

    BigDecimal price;
}