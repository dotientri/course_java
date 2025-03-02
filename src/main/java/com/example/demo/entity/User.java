package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;
import java.util.Set;

//da tao xong bang user
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
    Set<String> roles;

//Đây là một lớp đại diện cho thực thể User và ánh xạ với một bảng trong cơ sở dữ liệu. Các annotations như @Entity và @Id được sử dụng để định nghĩa thực thể và khóa chính của nó. @GeneratedValue(strategy = GenerationType.UUID) tạo ra một định danh duy nhất cho mỗi người dùng.
}
