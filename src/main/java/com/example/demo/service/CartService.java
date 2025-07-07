package com.example.demo.service;

import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper; // **QUAN TRỌNG**

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> createNewCartForUser(currentUser));
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> createNewCartForUser(currentUser));

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(newItem);
        }
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Transactional
    public CartResponse updateItem(Long productId, int quantity) {
        if (quantity < 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }
        Cart cart = getCartEntityForCurrentUser();

        CartItem itemToUpdate = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_IN_CART));

        if (quantity == 0) {
            // Nếu số lượng là 0, xóa sản phẩm khỏi giỏ hàng
            cart.getCartItems().remove(itemToUpdate);
        } else {
            itemToUpdate.setQuantity(quantity);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Transactional
    public CartResponse removeItem(Long productId) {
        // Xóa sản phẩm thực chất là cập nhật số lượng về 0
        return updateItem(productId, 0);
    }

    @Transactional
    public void clearCart() {
        Cart cart = getCartEntityForCurrentUser();
        cart.getCartItems().clear(); // orphanRemoval=true sẽ tự động xóa các CartItem trong DB
        cartRepository.save(cart);
    }

    // Helper method để lấy Cart entity, ném lỗi nếu không có
    private Cart getCartEntityForCurrentUser() {
        User currentUser = getCurrentUser();
        return cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }

    // Helper method để tạo giỏ hàng mới
    private Cart createNewCartForUser(User user) {
        Cart newCart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
        return cartRepository.save(newCart);
    }
}