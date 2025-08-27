// Sửa file: src/main/java/com/example/demo/mapper/ProductMapper.java
package com.example.demo.mapper;

import com.example.demo.dto.request.ProductCreationRequest;
import com.example.demo.dto.request.VariantCreationRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.VariantResponse;
import com.example.demo.entity.AttributeValue;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // --- MAPPING CHO VARIANT ---

    // Bỏ qua 'attributes' vì nó sẽ được xử lý trong Service
    @Mapping(target = "attributes", ignore = true)
    ProductVariant toProductVariant(VariantCreationRequest request);

    // Dùng custom method 'toAttributeMap' để chuyển Set<AttributeValue> thành Map
    @Mapping(source = "attributes", target = "attributes", qualifiedByName = "toAttributeMap")
    VariantResponse toVariantResponse(ProductVariant variant);

    @Named("toAttributeMap")
    default Map<String, String> toAttributeMap(Set<AttributeValue> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }
        return attributes.stream()
                .collect(Collectors.toMap(
                        av -> av.getAttribute().getName(), // Key: "Màu sắc"
                        AttributeValue::getValue            // Value: "Đỏ"
                ));
    }

    // --- MAPPING CHO PRODUCT ---
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toProduct(ProductCreationRequest request);

    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductCreationRequest request);
}