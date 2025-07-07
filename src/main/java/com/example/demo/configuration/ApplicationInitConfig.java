package com.example.demo.configuration;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Role; // Import thực thể Role
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.RoleRepository; // Import RoleRepository
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    // BƯỚC 1: Inject thêm RoleRepository và PasswordEncoder
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final PasswordEncoder passwordEncoder;
    final CartRepository cartRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            // BƯỚC 2: Khởi tạo 3 vai trò mặc định nếu chúng chưa tồn tại
            Role adminRole = createRoleIfNotFound("ADMIN", "Quản trị viên hệ thống");
            Role staffRole = createRoleIfNotFound("STAFF", "Nhân viên");
            Role userRole = createRoleIfNotFound("USER", "Người dùng mặc định");

            // BƯỚC 3: Khởi tạo người dùng ADMIN
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(Set.of(adminRole)) // Gán đối tượng Role, không phải String
                        .emailVerified(true) // Admin/Staff nên được xác thực sẵn
                        .build();
                User savedUser = userRepository.save(adminUser);
                createCartForUser(savedUser);
                log.warn("Tài khoản admin đã được tạo với mật khẩu mặc định: admin. Vui lòng thay đổi!");
            }

            // BƯỚC 4: Khởi tạo người dùng STAFF
            if (userRepository.findByUsername("staff").isEmpty()) {
                User staffUser = User.builder()
                        .username("staff")
                        .password(passwordEncoder.encode("staff"))
                        .roles(Set.of(staffRole)) // Gán đối tượng Role
                        .emailVerified(true)
                        .build();
                User savedUser = userRepository.save(staffUser);
                createCartForUser(savedUser);
                log.info("Tài khoản staff đã được tạo với mật khẩu mặc định: staff");
            }

            // BƯỚC 5: Khởi tạo người dùng USER mặc định
            if (userRepository.findByUsername("user").isEmpty()) {
                User defaultUser = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user"))
                        .roles(Set.of(userRole)) // Gán đối tượng Role
                        .emailVerified(true)
                        .build();
                User savedUser = userRepository.save(defaultUser);
                createCartForUser(savedUser);
                log.info("Tài khoản user mặc định đã được tạo với mật khẩu: user");
            }
        };
    }

    // Helper method để tránh lặp code khi tạo Role
    private Role createRoleIfNotFound(String name, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = Role.builder()
                    .name(name)
                    .description(description)
                    .permissions(new HashSet<>()) // Permission rỗng theo yêu cầu
                    .build();
            return roleRepository.save(newRole);
        });
    }

    // Helper method để tạo giỏ hàng
    private void createCartForUser(User user) {
        if (cartRepository.findByUser(user).isEmpty()) {
            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }
    }
}