package com.example.demo.mapper;

import com.example.demo.dto.request.ProductCreationRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    // Chuyển từ Request DTO sang Entity
    Product toProduct(ProductCreationRequest request);

    // Chuyển từ Entity sang Response DTO
    // Lấy tên category từ object category bên trong product
    // This is incorrect because the Category entity likely doesn't have a "categoryName" field
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toProductResponse(Product product);
}