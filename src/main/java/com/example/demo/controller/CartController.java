// Sửa file: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/controller/CartController.java
package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getMyCart() {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCartForCurrentUser())
                .build();
    }

    /**
     * Thêm một biến thể sản phẩm vào giỏ hàng.
     */
    @PostMapping("/items")
    public ApiResponse<CartResponse> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addItem(request.getVariantId(), request.getQuantity()))
                .message("Item added to cart successfully")
                .build();
    }

    /**
     * Cập nhật số lượng của một biến thể trong giỏ hàng.
     */
    @PutMapping("/items/{variantId}")
    public ApiResponse<CartResponse> updateItemInCart(@PathVariable Long variantId, @RequestParam @Min(0) int quantity) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateItem(variantId, quantity))
                .message("Cart item updated successfully")
                .build();
    }

    /**
     * Xóa một biến thể khỏi giỏ hàng.
     */
    @DeleteMapping("/items/{variantId}")
    public ApiResponse<Void> removeItemFromCart(@PathVariable Long variantId) {
        cartService.removeItem(variantId);
        return ApiResponse.<Void>builder()
                .message("Item removed from cart successfully")
                .build();
    }

    /**
     * Xóa toàn bộ sản phẩm khỏi giỏ hàng.
     */
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart() {
        cartService.clearCart();
        return ApiResponse.<Void>builder()
                .message("Cart cleared successfully")
                .build();
    }
}