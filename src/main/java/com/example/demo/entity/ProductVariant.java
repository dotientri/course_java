package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set; // <<< THÊM DÒNG NÀY ĐỂ SỬA LỖI

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long variantId;

    @Column(unique = true)
    String sku;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal price;

    @Column(precision = 10, scale = 2)
    BigDecimal salePrice;

    @Column(nullable = false)
    Integer stock;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "variant_images", joinColumns = @JoinColumn(name = "variant_id"))
    @Column(name = "image_url")
    List<String> images;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference("product-variant")
    Product product;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "variant_attributes",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    private Set<AttributeValue> attributes;
}