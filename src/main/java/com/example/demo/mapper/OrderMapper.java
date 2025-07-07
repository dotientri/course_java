package com.example.demo.mapper;

import com.example.demo.dto.response.OrderItemResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List; // Make sure this import is present

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // This mapping will now work because you added getFullAddress() to the Address entity
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "address.fullAddress", target = "shippingAddress")
    OrderResponse toOrderResponse(Order order);

    // This tells MapStruct how to map an OrderItem entity to an OrderItemResponse DTO
    @Mapping(source = "product.productId", target = "productId") // CORRECTED: Assumes the field in Product is "productId"
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.images", target = "imageUrl") // CHANGED: We now map the whole list and let the custom method below handle it.
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    // ADD THIS CUSTOM METHOD
    // This default method provides custom logic for MapStruct.
    // When it sees a mapping from List<String> (images) to String (imageUrl), it will use this method.
    default String mapImageUrlFromImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null; // Or you can return a URL to a default placeholder image
        }
        // Return the first image in the list as the thumbnail
        return images.get(0);
    }
}