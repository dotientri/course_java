package com.example.demo.mapper;

import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.AttributeValue;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // Ánh xạ từ Cart entity sang CartResponse DTO
    @Mapping(source = "cartItems", target = "items")
    CartResponse toCartResponse(Cart cart);

    // Ánh xạ từ CartItem entity sang CartItemResponse DTO
    @Mapping(source = "variant.product.productId", target = "productId")
    @Mapping(source = "variant.product.productName", target = "productName")
    @Mapping(source = "variant.variantId", target = "variantId")
    @Mapping(source = "variant.price", target = "price")
    @Mapping(source = "variant.stock", target = "stock")
    @Mapping(source = "variant.images", target = "imageUrl", qualifiedByName = "mapFirstImage")
    // THAY ĐỔI: Sử dụng các phương thức tùy chỉnh để lấy color và size
    @Mapping(source = "variant.attributes", target = "color", qualifiedByName = "mapColorFromAttributes")
    @Mapping(source = "variant.attributes", target = "size", qualifiedByName = "mapSizeFromAttributes")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    // Helper method để lấy ảnh đầu tiên từ danh sách
    @Named("mapFirstImage")
    default String mapFirstImage(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null; // Hoặc trả về một URL ảnh mặc định
        }
        return images.get(0);
    }

    // Helper method để trích xuất giá trị "Màu sắc" từ Set<AttributeValue>
    @Named("mapColorFromAttributes")
    default String mapColorFromAttributes(Set<AttributeValue> attributes) {
        if (attributes == null) {
            return null;
        }
        return attributes.stream()
                // Tìm thuộc tính có tên là "Màu sắc"
                .filter(attr -> "Màu sắc".equalsIgnoreCase(attr.getAttribute().getName()))
                // Lấy giá trị của nó
                .map(AttributeValue::getValue)
                // Lấy kết quả đầu tiên tìm được
                .findFirst()
                // Nếu không tìm thấy, trả về null
                .orElse(null);
    }

    // Helper method để trích xuất giá trị "Kích thước" từ Set<AttributeValue>
    @Named("mapSizeFromAttributes")
    default String mapSizeFromAttributes(Set<AttributeValue> attributes) {
        if (attributes == null) {
            return null;
        }
        return attributes.stream()
                // Tìm thuộc tính có tên là "Kích thước"
                .filter(attr -> "Kích thước".equalsIgnoreCase(attr.getAttribute().getName()))
                // Lấy giá trị của nó
                .map(AttributeValue::getValue)
                // Lấy kết quả đầu tiên tìm được
                .findFirst()
                // Nếu không tìm thấy, trả về null
                .orElse(null);
    }
}