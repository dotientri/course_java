package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;

//da tao xong bang user
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
//can annoustation de biet lam viec voi 1 bang
public class User {
    @Id
//de dinh nghia cho cai id
    @GeneratedValue(strategy = GenerationType.UUID)
//    uuid la nhung chuoi duoc ramdom ngau nhien
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
//Đây là một lớp đại diện cho thực thể User và ánh xạ với một bảng trong cơ sở dữ liệu. Các annotations như @Entity và @Id được sử dụng để định nghĩa thực thể và khóa chính của nó. @GeneratedValue(strategy = GenerationType.UUID) tạo ra một định danh duy nhất cho mỗi người dùng.
}
