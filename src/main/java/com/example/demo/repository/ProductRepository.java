// Tệp: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/repository/ProductRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductName(String productName);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.productId = :id")
    Optional<Product> findByIdWithVariants(@Param("id") Long id);

    List<Product> findByCategoryCategoryId(Long categoryId);

    // XÓA BỎ 2 PHƯƠNG THỨC CŨ NÀY
    // List<Product> findByVariants_ColorIgnoreCase(String color);
    // List<Product> findByVariants_SizeIgnoreCase(String size);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v JOIN v.attributes a_val JOIN a_val.attribute a " +
            "WHERE a.name = :attributeName AND a_val.value = :attributeValue")
    List<Product> findByAttribute(@Param("attributeName") String attributeName, @Param("attributeValue") String attributeValue);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))") // Cập nhật để tìm theo SKU
    List<Product> findByKeyword(@Param("keyword") String keyword);
}