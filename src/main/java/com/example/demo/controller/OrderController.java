// ... imports
import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.response.OrderResponse; // **Cần tạo DTO này**
import com.example.demo.dto.response.OrderItemResponse; // **Cần tạo DTO này**
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place")
    public ApiResponse<OrderResponse> placeOrder(@RequestParam Long addressId, @RequestParam String paymentMethod) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.placeOrder(addressId, paymentMethod)) // Service cần trả về DTO
                .message("Order placed successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders()) // Service cần trả về List<DTO>
                .build();
    }

    // ... áp dụng tương tự cho các phương thức còn lại ...
}