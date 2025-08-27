package com.example.demo.service;

import com.example.demo.dto.request.ProductCreationRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.AttributeValue;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductVariant;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.AttributeValueRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductMapper productMapper;
    private final AttributeValueRepository attributeValueRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreationRequest request) {
        if (productRepository.existsByProductName(request.getProductName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Product product = productMapper.toProduct(request);
        product.setCategory(category);

        List<ProductVariant> variants = request.getVariants().stream()
                .map(variantRequest -> {
                    ProductVariant variant = productMapper.toProductVariant(variantRequest);
                    variant.setProduct(product);

                    if (variantRequest.getAttributeValueIds() != null && !variantRequest.getAttributeValueIds().isEmpty()) {
                        Set<AttributeValue> attrs = new HashSet<>(attributeValueRepository.findAllById(variantRequest.getAttributeValueIds()));
                        if (attrs.size() != variantRequest.getAttributeValueIds().size()) {
                            throw new AppException(ErrorCode.ATTRIBUTE_VALUE_NOT_FOUND);
                        }
                        variant.setAttributes(attrs);
                    }
                    return variant;
                }).collect(Collectors.toList());

        product.setVariants(variants);

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductCreationRequest request) {
        Product product = findProductById(id);
        productMapper.updateProduct(product, request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        product.setCategory(category);

        product.getVariants().clear();

        List<ProductVariant> newVariants = request.getVariants().stream()
                .map(variantRequest -> {
                    ProductVariant variant = productMapper.toProductVariant(variantRequest);
                    variant.setProduct(product);

                    if (variantRequest.getAttributeValueIds() != null && !variantRequest.getAttributeValueIds().isEmpty()) {
                        Set<AttributeValue> attrs = new HashSet<>(attributeValueRepository.findAllById(variantRequest.getAttributeValueIds()));
                        if (attrs.size() != variantRequest.getAttributeValueIds().size()) {
                            throw new AppException(ErrorCode.ATTRIBUTE_VALUE_NOT_FOUND);
                        }
                        variant.setAttributes(attrs);
                    }
                    return variant;
                }).collect(Collectors.toList());

        product.getVariants().addAll(newVariants);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }

    @Transactional
    public void addImageToVariant(Long variantId, String imageName) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_EXISTED));
        variant.getImages().add(imageName);
        variantRepository.save(variant);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        productRepository.deleteById(id);
    }

    public ProductResponse getProduct(Long id) {
        Product product = findProductById(id);
        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    // PHƯƠNG THỨC THAY THẾ CHO findByColor và findBySize
    public List<ProductResponse> findByAttribute(String attributeName, String attributeValue) {
        return productRepository.findByAttribute(attributeName, attributeValue).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByKeyword(String keyword) {
        return productRepository.findByKeyword(keyword).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    private Product findProductById(Long id) {
        return productRepository.findByIdWithVariants(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
    }
}