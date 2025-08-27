// C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/service/OrderService.java
package com.example.demo.service;

import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse placeOrder(Long addressId, String paymentMethod) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Address shippingAddress = addressRepository.findByIdAndUser(addressId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        Order newOrder = new Order();
        newOrder.setUser(currentUser);
        newOrder.setAddress(shippingAddress);
        newOrder.setOrderDate(java.time.LocalDate.now());
        newOrder.setTotalAmount(cart.getTotalPrice());
        newOrder.setPaymentMethod(paymentMethod);

        // === FIX QUAN TRỌNG: Đặt trạng thái chính xác ===
        if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
            newOrder.setStatus("PENDING_PAYMENT"); // Phải là PENDING_PAYMENT để logic IPN hoạt động
        } else {
            newOrder.setStatus("PENDING"); // Trạng thái cho các phương thức khác (ví dụ: COD)
        }

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    ProductVariant variant = cartItem.getVariant();
                    if (variant.getStock() < cartItem.getQuantity()) {
                        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
                    }
                    // Trừ tồn kho
                    variant.setStock(variant.getStock() - cartItem.getQuantity());
                    variantRepository.save(variant);

                    return OrderItem.builder()
                            .order(newOrder)
                            .variant(variant)
                            .quantity(cartItem.getQuantity())
                            .priceAtOrder(variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice())
                            .build();
                }).collect(Collectors.toList());

        newOrder.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(newOrder);

        // Xóa giỏ hàng sau khi đã đặt hàng thành công
        cartRepository.delete(cart);

        log.info("User '{}' placed order successfully with ID: {}", currentUser.getUsername(), savedOrder.getId());
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional
    public void confirmPayment(Long orderId, long vnpayAmount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!"PENDING_PAYMENT".equalsIgnoreCase(order.getStatus())) {
            log.warn("Order {} already processed or has an invalid status, skipping IPN.", orderId);
            return;
        }

        // FIX: Convert BigDecimal to double before rounding
        long orderAmount = Math.round(order.getTotalAmount().doubleValue());
        if (orderAmount != vnpayAmount) {
            log.error("Amount mismatch for order {}. Expected: {}, Actual: {}", orderId, orderAmount, vnpayAmount);
            order.setStatus("PAYMENT_FAILED_AMOUNT_MISMATCH");
            orderRepository.save(order);
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        order.setStatus("PAID");
        orderRepository.save(order);
        log.info("Payment confirmed for orderId: {}. Status updated to PAID.", orderId);
    }

    // ... other methods ...
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(currentUser);
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isAdmin && !Objects.equals(order.getUser().getId(), currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order ID {} status updated to '{}' by an administrator.", updatedOrder.getId(), status);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}