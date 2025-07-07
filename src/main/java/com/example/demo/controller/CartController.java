package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Lấy thông tin giỏ hàng của người dùng hiện tại.
     * Service sẽ tự động xử lý việc tạo giỏ hàng mới nếu chưa có.
     */
    @GetMapping
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart())
                .build();
    }

    /**
     * Thêm một sản phẩm vào giỏ hàng hoặc tăng số lượng nếu đã tồn tại.
     */
    @PostMapping("/add")
    public ApiResponse<CartResponse> addItem(@RequestParam Long productId, @RequestParam int quantity) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addItem(productId, quantity))
                .message("Item added to cart")
                .build();
    }

    /**
     * Cập nhật số lượng của một sản phẩm trong giỏ hàng.
     * Nếu số lượng là 0, sản phẩm sẽ bị xóa.
     */
    @PatchMapping("/update")
    public ApiResponse<CartResponse> updateItem(@RequestParam Long productId, @RequestParam int quantity) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateItem(productId, quantity))
                .message("Cart item updated")
                .build();
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng.
     */
    @DeleteMapping("/remove")
    public ApiResponse<CartResponse> removeItem(@RequestParam Long productId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeItem(productId))
                .message("Item removed from cart")
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