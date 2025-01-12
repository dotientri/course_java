package com.example.demo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
//cái aunomation nào sẽ get set cho các private
public class UserCreationRequest {
//    @Size(min = 8, max = 20,message = "Username must be least 8 character")
//cach de su dung
    @Size(min = 3, max = 20,message = "USERNAME_INVALID")
     String username;
    @Size(min = 3, max = 15,message = "INVALID_PASSWORD")
     String password;
     String firstName;
     String lastName;
     LocalDate dob;
//Đây là một lớp DTO (Data Transfer Object) được sử dụng để đóng gói dữ liệu cần thiết để tạo người dùng mới. Nó chứa các trường dữ liệu cho tên đăng nhập, mật khẩu, tên, họ và ngày sinh của người dùng, cùng với các phương thức getter và setter tương ứng.

}
