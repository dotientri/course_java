// TẠO FILE MỚI: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/repository/ProductVariantRepository.java
package com.example.demo.repository;

import com.example.demo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    // Có thể thêm các method tìm kiếm theo SKU, color, size... sau này
}