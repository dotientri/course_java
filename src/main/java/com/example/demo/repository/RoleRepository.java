// C:/Users/dotie/IdeaProjects/course_java/src/main/java/com/example/demo/repository/RoleRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    // THÊM PHƯƠNG THỨC NÀY VÀO
    Optional<Role> findByName(String name);
}