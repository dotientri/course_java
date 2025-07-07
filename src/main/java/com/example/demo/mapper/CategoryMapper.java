package com.example.demo.mapper;

import com.example.demo.dto.request.CategoryRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    // Chuyển từ DTO Request sang Entity để lưu vào DB
    Category toCategory(CategoryRequest request);

    // Chuyển từ Entity sang DTO Response để trả về cho client
    CategoryResponse toCategoryResponse(Category category);

    // Cập nhật một entity có sẵn từ DTO request
    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}