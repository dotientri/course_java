package com.example.demo.configuration;

import com.example.demo.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/products",
            "/products/{id}",
            "/products/category/{categoryId}",
            "/products/color",
            "/products/size",
            "/products/search",
            "/products/images/{fileName:.+}",
            "/categories",
            "/categories/{id}",
            "/v3/api-docs/**", // Dành cho Swagger/OpenAPI
            "/swagger-ui/**"   // Dành cho Swagger/OpenAPI
    };


    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    // === THÊM DÒNG NÀY ===
    // Tiêm giá trị từ application.properties vào biến frontendUrl
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Cấu hình CORS rõ ràng hơn
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        // Các endpoint xác thực luôn public
                        .requestMatchers("/auth/**", "/payments/**").permitAll() // Gộp payments vào đây
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        // --- QUYỀN CỦA USER (Bao gồm cả ADMIN) ---
                        .requestMatchers("/users/info").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/cart/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/orders/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/addresses/**").hasAnyRole("USER", "ADMIN")

                        // --- QUYỀN CỦA ADMIN ---
                        .requestMatchers(HttpMethod.POST, "/products", "/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/{id}", "/categories/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/{id}", "/categories/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/variants/{variantId}/upload").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/orders/{orderId}/status").hasRole("ADMIN")
                        .requestMatchers("/users", "/users/{userId}").hasRole("ADMIN")
                        .requestMatchers("/roles/**", "/permission/**").hasRole("ADMIN")

                        // Các endpoint khác đã được bảo vệ bằng @PreAuthorize, nên chỉ cần authenticated
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // === SỬA DÒNG NÀY ===
        // Sử dụng biến đã được tiêm thay vì hardcode
        configuration.addAllowedOrigin(frontendUrl);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}