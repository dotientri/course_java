package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.request.ProductCreationRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.IStorageService;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final IStorageService storageService;

    // =================== PUBLIC ENDPOINTS ===================
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getAllProducts())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(id))
                .build();
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ProductResponse>> getByCategory(@PathVariable Long categoryId) {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.findByCategory(categoryId))
                .build();
    }

    @GetMapping("/color")
    public ApiResponse<List<ProductResponse>> getByColor(@RequestParam String color) {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.findByColor(color))
                .build();
    }

    @GetMapping("/size")
    public ApiResponse<List<ProductResponse>> getBySize(@RequestParam String size) {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.findBySize(size))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.findByKeyword(keyword))
                .build();
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName) {
        byte[] bytes = storageService.readFileContent(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    // =================== ADMIN-ONLY ENDPOINTS ===================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductCreationRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(productService.createProduct(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductCreationRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(id, request))
                .build();
    }

    @PostMapping("/upload/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String generatedFileName = storageService.storeFile(file);
        productService.addProductImage(id, generatedFileName);
        String imageUrl = "/products/images/" + generatedFileName;
        return ApiResponse.<String>builder()
                .message("Image uploaded successfully for product id: " + id)
                .result(imageUrl)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Product deleted successfully.")
                .build();
    }
}