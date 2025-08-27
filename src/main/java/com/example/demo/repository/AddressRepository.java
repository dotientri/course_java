// Sửa file: src/main/java/com/example/demo/repository/AddressRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    // Tìm tất cả địa chỉ của một người dùng
    List<Address> findByUser(User user);

    // Tìm một địa chỉ cụ thể của một người dùng (QUAN TRỌNG cho bảo mật)
    Optional<Address> findByIdAndUser(Long id, User user);

    // Bỏ tất cả các địa chỉ khác của người dùng khỏi trạng thái mặc định
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user = :user AND a.isDefault = true")
    void unsetAllDefaultsForUser(User user);
}