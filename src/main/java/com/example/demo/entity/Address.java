// src/main/java/com/example/demo/entity/Address.java
package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String street;
    @Column(nullable = false)
    String ward;
    @Column(nullable = false)
    String district;
    @Column(nullable = false)
    String city;
    @Column(nullable = false)
    String postalCode;

    @ManyToOne
    @JsonBackReference("user-address")
    User user;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("address-orders")
    List<Order> orders;

    public String getFullAddress() {
        // You can customize the format as you like
        return street + ", " + city + ", " + " " + postalCode;
    }
}