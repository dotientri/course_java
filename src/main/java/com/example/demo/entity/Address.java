package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // CÁC TRƯỜNG MỚI ĐƯỢC THÊM VÀO ĐỂ ĐỒNG BỘ VỚI DTO
    String fullName;
    String phoneNumber;
    String street;
    String ward;
    String district;
    String province;
    boolean isDefault; // Dùng kiểu nguyên thủy để đảm bảo không bị null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // Giữ lại mối quan hệ với Order nếu cần
    @OneToMany(mappedBy = "address")
    List<Order> orders;
}