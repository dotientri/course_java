// TẠO FILE MỚI hoặc SỬA LẠI: src/main/java/com/example/demo/service/CartService.java
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

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Transactional
    public CartResponse addItem(Long variantId, int quantity) {
        Cart cart = getOrCreateCartForCurrentUser();
        ProductVariant variant = findVariantById(variantId);

        if (variant.getStock() < quantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getVariant().getVariantId().equals(variantId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            if (variant.getStock() < newQuantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setVariant(variant);
            newItem.setQuantity(quantity);
            cart.getCartItems().add(cartItemRepository.save(newItem));
        }

        updateCartTotal(cart);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(Long variantId, int quantity) {
        Cart cart = getOrCreateCartForCurrentUser();
        CartItem itemToUpdate = findCartItemByVariantId(cart, variantId);

        if (quantity == 0) {
            return removeItem(variantId);
        }

        ProductVariant variant = itemToUpdate.getVariant();
        if (variant.getStock() < quantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        itemToUpdate.setQuantity(quantity);
        cartItemRepository.save(itemToUpdate);
        updateCartTotal(cart);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(Long variantId) {
        Cart cart = getOrCreateCartForCurrentUser();
        CartItem itemToRemove = findCartItemByVariantId(cart, variantId);

        cart.getCartItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        updateCartTotal(cart);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart() {
        Cart cart = getOrCreateCartForCurrentUser();
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    public CartResponse getCartForCurrentUser() {
        return cartMapper.toCartResponse(getOrCreateCartForCurrentUser());
    }

    // --- Helper Methods ---

    private Cart getOrCreateCartForCurrentUser() {
        User currentUser = getCurrentUser();
        return cartRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(currentUser);
                    return cartRepository.save(newCart);
                });
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(item -> {
                    BigDecimal price = Optional.ofNullable(item.getVariant().getSalePrice())
                            .orElse(item.getVariant().getPrice());
                    return price.multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private ProductVariant findVariantById(Long variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_EXISTED));
    }

    private CartItem findCartItemByVariantId(Cart cart, Long variantId) {
        return cart.getCartItems().stream()
                .filter(item -> item.getVariant().getVariantId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_IN_CART));
    }
}