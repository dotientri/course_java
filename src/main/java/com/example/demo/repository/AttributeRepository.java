// TẠO FILE MỚI: src/main/java/com/example/demo/repository/AttributeRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Optional<Attribute> findByName(String name);
}