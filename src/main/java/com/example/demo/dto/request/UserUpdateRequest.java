package com.example.demo.dto.request;

import com.example.demo.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
     @Size(min = 3, max = 15,message = "INVALID_PASSWORD")
     String username;
     String password;
     String firstName;
     String lastName;
     @DobConstraint(min = 18, message = "INVALID_DOB")
     LocalDate dob;
     List<String> roles; // List<String>


}
