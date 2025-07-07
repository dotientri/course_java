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

    // Các repository và mapper cần thiết
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse placeOrder(Long addressId, String paymentMethod) {
        // 1. Lấy thông tin người dùng hiện tại từ Spring Security
        User currentUser = getCurrentUser();

        // 2. Tìm giỏ hàng của người dùng và kiểm tra xem có trống không
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // 3. Tìm địa chỉ giao hàng, đảm bảo địa chỉ này thuộc về người dùng hiện tại
        Address shippingAddress = addressRepository.findByIdAndUser(addressId, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // 4. Tạo đối tượng Order mới
        Order newOrder = new Order();
        newOrder.setUser(currentUser);
        newOrder.setAddress(shippingAddress);
        newOrder.setOrderDate(java.time.LocalDate.now());
        newOrder.setStatus("PENDING"); // Trạng thái ban đầu
        newOrder.setTotalAmount(cart.getTotalPrice());
        newOrder.setPaymentMethod(paymentMethod);

        // 5. Chuyển các sản phẩm từ CartItem sang OrderItem bằng Stream API cho code gọn hơn
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(newOrder);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getProduct().getPrice()); // Lấy giá tại thời điểm đặt hàng
                    return orderItem;
                }).collect(Collectors.toList());

        newOrder.setItems(orderItems);

        // 6. Lưu Order và các OrderItem (nhờ CascadeType.ALL trong Entity)
        Order savedOrder = orderRepository.save(newOrder);

        // 7. Xóa giỏ hàng sau khi đã đặt hàng thành công
        cartRepository.delete(cart);

        // 8. Chuyển đổi Order entity đã lưu sang OrderResponse DTO để trả về
        log.info("User '{}' placed order successfully with ID: {}", currentUser.getUsername(), savedOrder.getId());
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(currentUser);

        // Chuyển đổi danh sách Order entities sang danh sách OrderResponse DTOs
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // **BỔ SUNG QUAN TRỌNG:** Kiểm tra quyền truy cập
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        // Cho phép truy cập nếu là admin hoặc nếu đơn hàng này thuộc về người dùng hiện tại
        if (!isAdmin && !Objects.equals(order.getUser().getId(), currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        // Logic này thường chỉ dành cho Admin
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order ID {} status updated to '{}' by an administrator.", updatedOrder.getId(), status);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    // Helper method để tránh lặp code lấy thông tin người dùng
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}