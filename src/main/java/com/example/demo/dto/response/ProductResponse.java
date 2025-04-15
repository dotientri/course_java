package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String product_id;
    String product_name;
    String product_description;
    String product_price;
    List<String> product_image;
    String product_category;
    String product_type;
}
