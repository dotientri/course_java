package com.example.demo.repository;

import com.example.demo.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products , String> {

}
