package com.example.demo.mapper;

import com.example.demo.dto.response.CartResponse;
import com.example.demo.dto.response.OrderItemResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // Ánh xạ từ CartItem entity sang OrderItemResponse DTO
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.images", target = "imageUrl", qualifiedByName = "mapFirstImage")
    @Mapping(source = "product.price", target = "price") // **SỬA LỖI TẠI ĐÂY**
    OrderItemResponse toOrderItemResponse(CartItem cartItem);

    // Ánh xạ từ Cart entity sang CartResponse DTO
    @Mapping(source = "cartItems", target = "items")
    CartResponse toCartResponse(Cart cart);

    // Helper method để lấy ảnh đầu tiên từ danh sách
    @Named("mapFirstImage")
    default String mapFirstImage(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null; // Hoặc trả về một URL ảnh mặc định
        }
        return images.get(0);
    }
}