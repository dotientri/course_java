// TẠO FILE MỚI: src/main/java/com/example/demo/repository/AttributeValueRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Attribute;
import com.example.demo.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    Optional<AttributeValue> findByAttributeAndValue(Attribute attribute, String value);
}