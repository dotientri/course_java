package com.example.demo.repository;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // **QUAN TRỌNG**

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUser(User user);

    // **SỬA LẠI:** Dùng Optional để xử lý trường hợp không tìm thấy
    Optional<Address> findByIdAndUser(Long addressId, User currentUser);
}