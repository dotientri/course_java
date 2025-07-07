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
    private final CategoryMapper categoryMapper; // **QUAN TRỌNG:** Tiêm Mapper vào

    public CategoryResponse createCategory(CategoryRequest request) {
        // 1. Dùng mapper để chuyển DTO request thành Entity
        Category category = categoryMapper.toCategory(request);

        // 2. Lưu entity vào DB
        Category savedCategory = categoryRepository.save(category);
        log.info("Category has been created with ID: {}", savedCategory.getCategoryId());

        // 3. Dùng mapper để chuyển entity đã lưu thành DTO response
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        // 1. Tìm entity cần cập nhật
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // 2. Dùng mapper để cập nhật các trường của entity từ DTO
        categoryMapper.updateCategory(category, request);

        // 3. Lưu lại và trả về DTO response
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
        // 1. Tìm entity
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        // 2. Chuyển sang DTO response
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        // 1. Lấy tất cả entities
        List<Category> categories = categoryRepository.findAll();
        // 2. Dùng stream và mapper để chuyển cả danh sách sang DTO response
        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
}