// Tệp: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/configuration/ApplicationInitConfig.java
package com.example.demo.configuration;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    // ... (Phần inject và applicationRunner() giữ nguyên)
    UserRepository userRepository;
    RoleRepository roleRepository;
    CategoryRepository categoryRepository;
    PasswordEncoder passwordEncoder;
    CartRepository cartRepository;
    ProductRepository productRepository;
    AttributeRepository attributeRepository;
    AttributeValueRepository attributeValueRepository;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner() {
        return args -> {
            // --- 1. Khởi tạo Role ---
            Role adminRole = createRoleIfNotFound("ADMIN", "Quản trị viên hệ thống");
            Role staffRole = createRoleIfNotFound("STAFF", "Nhân viên");
            Role userRole = createRoleIfNotFound("USER", "Người dùng mặc định");

            // --- 2. Khởi tạo User ---
            createUsers(adminRole, staffRole, userRole);

            // --- 3. Khởi tạo Category ---
            createDefaultCategories();

            // --- 4. Khởi tạo Attributes và Values ---
            createDefaultAttributes();

            // --- 5. Khởi tạo Product và ProductVariant ---
            log.info("Initializing default products with variants...");
            createDefaultProducts();
            log.info("Default products initialized successfully.");
        };
    }

    // ... (Các phương thức createDefaultAttributes và createDefaultProducts giữ nguyên)
    private void createDefaultAttributes() {
        log.info("Initializing default attributes...");
        Attribute colorAttribute = createAttributeIfNotFound("Màu sắc");
        Attribute sizeAttribute = createAttributeIfNotFound("Kích thước");
        createAttributeValueIfNotFound(colorAttribute, "Nâu", null);
        createAttributeValueIfNotFound(colorAttribute, "Trắng", "#FFFFFF");
        createAttributeValueIfNotFound(colorAttribute, "Đen", "#000000");
        createAttributeValueIfNotFound(colorAttribute, "Xanh Dương", "#0000FF");
        createAttributeValueIfNotFound(colorAttribute, "Xanh Đậm", null);
        createAttributeValueIfNotFound(sizeAttribute, "S", null);
        createAttributeValueIfNotFound(sizeAttribute, "M", null);
        createAttributeValueIfNotFound(sizeAttribute, "L", null);
        createAttributeValueIfNotFound(sizeAttribute, "XL", null);
        createAttributeValueIfNotFound(sizeAttribute, "28", null);
        createAttributeValueIfNotFound(sizeAttribute, "30", null);
        createAttributeValueIfNotFound(sizeAttribute, "32", null);
        log.info("Default attributes initialized.");
    }

    private void createDefaultProducts() {
        Attribute colorAttr = attributeRepository.findByName("Màu sắc").orElseThrow();
        Attribute sizeAttr = attributeRepository.findByName("Kích thước").orElseThrow();
        AttributeValue nau = attributeValueRepository.findByAttributeAndValue(colorAttr, "Nâu").orElseThrow();
        AttributeValue trang = attributeValueRepository.findByAttributeAndValue(colorAttr, "Trắng").orElseThrow();
        AttributeValue den = attributeValueRepository.findByAttributeAndValue(colorAttr, "Đen").orElseThrow();
        AttributeValue xanhDuong = attributeValueRepository.findByAttributeAndValue(colorAttr, "Xanh Dương").orElseThrow();
        AttributeValue xanhDam = attributeValueRepository.findByAttributeAndValue(colorAttr, "Xanh Đậm").orElseThrow();
        AttributeValue sizeS = attributeValueRepository.findByAttributeAndValue(sizeAttr, "S").orElseThrow();
        AttributeValue sizeM = attributeValueRepository.findByAttributeAndValue(sizeAttr, "M").orElseThrow();
        AttributeValue sizeL = attributeValueRepository.findByAttributeAndValue(sizeAttr, "L").orElseThrow();
        AttributeValue sizeXL = attributeValueRepository.findByAttributeAndValue(sizeAttr, "XL").orElseThrow();
        AttributeValue size28 = attributeValueRepository.findByAttributeAndValue(sizeAttr, "28").orElseThrow();
        AttributeValue size30 = attributeValueRepository.findByAttributeAndValue(sizeAttr, "30").orElseThrow();
        AttributeValue size32 = attributeValueRepository.findByAttributeAndValue(sizeAttr, "32").orElseThrow();
        createProductWithVariants("Áo Polo Năng Động", "Áo polo lịch sự, phù hợp đi làm và đi chơi", 1L, List.of(new VariantData("POLO-NAU-M", "260000", "230000", 50, List.of(nau.getId(), sizeM.getId()), List.of("https://cdn2.yame.vn/pimg/ao-polo-co-be-tay-ngan-soi-nhan-tao-giu-am-bieu-tuong-dang-rong-gia-tot-no-style-m128-0023882/82de5b58-6134-1a03-c46c-001c890f99df.jpg?w=540&h=756&c=true&v=052025")), new VariantData("POLO-NAU-L", "260000", "230000", 50, List.of(nau.getId(), sizeL.getId()), List.of("https://cdn2.yame.vn/pimg/ao-polo-co-be-tay-ngan-soi-nhan-tao-giu-am-bieu-tuong-dang-rong-gia-tot-no-style-m128-0023882/82de5b58-6134-1a03-c46c-001c890f99df.jpg?w=540&h=756&c=true&v=052025")), new VariantData("POLO-NAU-XL", "260000", "230000", 30, List.of(nau.getId(), sizeXL.getId()), List.of("https://cdn2.yame.vn/pimg/ao-polo-co-be-tay-ngan-soi-nhan-tao-giu-am-bieu-tuong-dang-rong-gia-tot-no-style-m128-0023882/82de5b58-6134-1a03-c46c-001c890f99df.jpg?w=540&h=756&c=true&v=052025"))));
        createProductWithVariants("Áo Thun Cổ Tròn", "Áo thun cotton thoáng mát, dễ phối đồ", 1L, List.of(new VariantData("AT-TRANG-S", "220000", "180000", 100, List.of(trang.getId(), sizeS.getId()), List.of("https://cdn2.yame.vn/pimg/ao-thun-co-be-tay-ngan-soi-nhan-tao-co-gian-tron-dang-vua-gia-tot-no-brand-03-0023744/0ec6ee0b-0e05-f602-cd2c-001c890defac.jpg?w=540&h=756&c=true&v=052025")), new VariantData("AT-DEN-M", "220000", "180000", 80, List.of(den.getId(), sizeM.getId()), List.of("https://cdn2.yame.vn/pimg/ao-thun-co-be-tay-ngan-soi-nhan-tao-co-gian-tron-dang-vua-gia-tot-no-brand-03-0023742/2dbc8d49-a89f-ea02-472e-001c890d7c57.jpg?w=540&h=756&c=true&v=052025")), new VariantData("AT-XANH-L", "220000", "180000", 70, List.of(xanhDuong.getId(), sizeL.getId()), List.of("https://cdn2.yame.vn/pimg/ao-thun-co-be-tay-ngan-soi-nhan-tao-co-gian-tron-dang-vua-gia-tot-no-brand-03-0023745/03e6cff0-696b-fc02-c089-001c890e4414.jpg?w=540&h=756&c=true&v=052025"))));
        createProductWithVariants("Quần Jeans", "Quần jeans phong cách đường phố, chất liệu denim cao cấp", 3L, List.of(new VariantData("JEAN-XANH-28", "400000", "350000", 40, List.of(xanhDam.getId(), size28.getId()), List.of("https://cdn2.yame.vn/pimg/quan-jean-the-original-003-vol-25-0024470/6dd8193c-3494-2e00-3910-001c96457ca5.jpg?w=540&h=756&c=true&v=052025")), new VariantData("JEAN-XANH-30", "400000", "350000", 50, List.of(xanhDam.getId(), size30.getId()), List.of("https://cdn2.yame.vn/pimg/quan-jean-the-original-003-vol-25-0024470/6dd8193c-3494-2e00-3910-001c96457ca5.jpg?w=540&h=756&c=true&v=052025")), new VariantData("JEAN-XANH-32", "410000", "360000", 30, List.of(xanhDam.getId(), size32.getId()), List.of("https://cdn2.yame.vn/pimg/quan-jean-the-original-003-vol-25-0024470/6dd8193c-3494-2e00-3910-001c96457ca5.jpg?w=540&h=756&c=true&v=052025"))));
    }

    private void createProductWithVariants(String productName, String description, Long categoryId, List<VariantData> variantsData) {
        if (productRepository.existsByProductName(productName)) {
            log.info("Product '{}' already exists. Skipping.", productName);
            return;
        }
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("FATAL: Category with ID " + categoryId + " not found during data seeding."));
        Product product = Product.builder().productName(productName).description(description).category(category).build();
        List<ProductVariant> variants = variantsData.stream().map(data -> {
            Set<AttributeValue> attributes = new HashSet<>(attributeValueRepository.findAllById(data.attributeValueIds));
            return ProductVariant.builder().sku(data.sku).price(new BigDecimal(data.price)).salePrice(data.salePrice != null ? new BigDecimal(data.salePrice) : null).stock(data.stock).images(data.images).attributes(attributes).product(product).build();
        }).toList();
        product.setVariants(variants);
        productRepository.save(product);
        log.info("Product '{}' with {} variants created.", productName, variants.size());
    }

    private record VariantData(String sku, String price, String salePrice, int stock, List<Long> attributeValueIds, List<String> images) {}

    // =================== PHẦN SỬA LỖI ===================
    // Cung cấp lại phần thân cho các phương thức helper

    private void createDefaultCategories() {
        log.info("Initializing default categories...");
        createCategoryIfNotFound("Áo Thun & Áo Polo", "Bộ sưu tập áo thun và áo polo đa dạng, từ cơ bản đến họa tiết.");
        createCategoryIfNotFound("Áo Sơ Mi", "Những chiếc áo sơ mi thanh lịch cho công sở và dạo phố.");
        createCategoryIfNotFound("Quần Jeans & Quần Dài", "Các kiểu dáng quần jeans và quần dài thời thượng, thoải mái.");
        createCategoryIfNotFound("Váy & Đầm", "Tỏa sáng trong những bộ váy và đầm nữ tính, sành điệu.");
        createCategoryIfNotFound("Phụ Kiện", "Hoàn thiện bộ trang phục với các phụ kiện như mũ, thắt lưng, túi xách.");
        log.info("Default categories initialized successfully.");
    }

    private void createCategoryIfNotFound(String name, String description) {
        if (categoryRepository.findByName(name).isEmpty()) {
            Category newCategory = Category.builder()
                    .name(name)
                    .description(description)
                    .build();
            categoryRepository.save(newCategory);
            log.info("Category '{}' created.", name);
        }
    }

    private void createUsers(Role adminRole, Role staffRole, Role userRole) {
        createUserIfNotFound("admin", "admin", adminRole, true);
        createUserIfNotFound("staff", "staff", staffRole, true);
        createUserIfNotFound("user", "user", userRole, true);
    }

    // Tệp: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/configuration/ApplicationInitConfig.java

// ...

    // Tệp: C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/configuration/ApplicationInitConfig.java

// ...

    private void createUserIfNotFound(String username, String password, Role role, boolean emailVerified) {
        userRepository.findByUsername(username).or(() -> {
            // TẠO EMAIL MẶC ĐỊNH ĐỂ ĐẢM BẢO DỮ LIỆU NHẤT QUÁN
            // Điều này cực kỳ quan trọng để tránh các lỗi ràng buộc ngầm trong DB.
            String email = username + "@default.com";

            User user = User.builder()
                    .username(username)
                    .email(email) // <-- THÊM DÒNG NÀY
                    .password(passwordEncoder.encode(password))
                    .roles(Set.of(role))
                    .emailVerified(emailVerified)
                    .build();
            User savedUser = userRepository.save(user);
            createCartForUser(savedUser);
            log.info("Account '{}' has been created with email '{}' and default password: {}",
                    username, email, password);
            return Optional.of(savedUser);
        });
    }

// ...

// ...

    private Role createRoleIfNotFound(String name, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = Role.builder()
                    .name(name)
                    .description(description)
                    .permissions(new HashSet<>())
                    .build();
            return roleRepository.save(newRole);
        });
    }

    private void createCartForUser(User user) {
        if (cartRepository.findByUser(user).isEmpty()) {
            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }
    }

    private Attribute createAttributeIfNotFound(String name) {
        return attributeRepository.findByName(name)
                .orElseGet(() -> attributeRepository.save(Attribute.builder().name(name).build()));
    }

    private void createAttributeValueIfNotFound(Attribute attribute, String value, String metadata) {
        attributeValueRepository.findByAttributeAndValue(attribute, value)
                .ifPresentOrElse(
                        (val) -> {}, // Nếu đã tồn tại, không làm gì cả
                        () -> attributeValueRepository.save(AttributeValue.builder()
                                .attribute(attribute)
                                .value(value)
                                .metadata(metadata)
                                .build())
                );
    }
}