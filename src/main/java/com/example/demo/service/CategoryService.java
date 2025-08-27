// C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/service/CategoryService.java
package com.example.demo.service;

import com.example.demo.dto.request.CategoryRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse createCategory(CategoryRequest request) {
        // Kiểm tra xem tên danh mục đã tồn tại chưa
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        // 1. Dùng mapper để chuyển DTO request thành Entity
        Category category = categoryMapper.toCategory(request);
        // 2. Lưu entity vào DB
        Category savedCategory = categoryRepository.save(category);
        log.info("Category has been created with ID: {}", savedCategory.getCategoryId());
        // 3. Dùng mapper để chuyển entity đã lưu thành DTO response
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    // ... các phương thức khác không thay đổi
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Optional: Check if the new name is being used by another category
        categoryRepository.findByName(request.getName()).ifPresent(existingCategory -> {
            if (!existingCategory.getCategoryId().equals(id)) {
                throw new AppException(ErrorCode.CATEGORY_EXISTED);
            }
        });

        categoryMapper.updateCategory(category, request);

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category with ID: {} has been updated", updatedCategory.getCategoryId());
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        categoryRepository.deleteById(id);
        log.info("Category with ID: {} has been deleted", id);
    }

    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
}