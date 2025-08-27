package com.example.demo.mapper;

import com.example.demo.dto.response.OrderItemResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Address; // QUAN TRỌNG: Thêm import này
import com.example.demo.entity.AttributeValue;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "id", target = "orderId")
    // THAY ĐỔI: Sử dụng phương thức tùy chỉnh để tạo địa chỉ giao hàng
    @Mapping(source = "address", target = "shippingAddress", qualifiedByName = "mapAddressToString")
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toOrderResponse(Order order);

    // Ánh xạ từ OrderItem entity sang OrderItemResponse DTO
    @Mapping(source = "variant.product.productId", target = "productId")
    @Mapping(source = "variant.product.productName", target = "productName")
    @Mapping(source = "variant.variantId", target = "variantId")
    @Mapping(source = "priceAtOrder", target = "price") // Lấy giá đã lưu tại thời điểm đặt hàng
    @Mapping(source = "variant.images", target = "imageUrl", qualifiedByName = "mapFirstImage")
    @Mapping(source = "variant.attributes", target = "color", qualifiedByName = "mapColorFromAttributes")
    @Mapping(source = "variant.attributes", target = "size", qualifiedByName = "mapSizeFromAttributes")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

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
                .filter(attr -> "Màu sắc".equalsIgnoreCase(attr.getAttribute().getName()))
                .map(AttributeValue::getValue)
                .findFirst()
                .orElse(null);
    }

    // Helper method để trích xuất giá trị "Kích thước" từ Set<AttributeValue>
    @Named("mapSizeFromAttributes")
    default String mapSizeFromAttributes(Set<AttributeValue> attributes) {
        if (attributes == null) {
            return null;
        }
        return attributes.stream()
                .filter(attr -> "Kích thước".equalsIgnoreCase(attr.getAttribute().getName()))
                .map(AttributeValue::getValue)
                .findFirst()
                .orElse(null);
    }

    // THÊM MỚI: Helper method để chuyển đổi Address entity thành chuỗi địa chỉ đầy đủ
    @Named("mapAddressToString")
    default String mapAddressToString(Address address) {
        if (address == null) {
            return null;
        }
        return String.join(", ",
                address.getStreet(),
                address.getWard(),
                address.getDistrict(),
                address.getProvince());
    }
}