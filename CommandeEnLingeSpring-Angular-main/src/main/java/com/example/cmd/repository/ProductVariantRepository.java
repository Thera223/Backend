package com.example.cmd.repository;

import com.example.cmd.model.ProductVariant;
import com.example.cmd.model.SousCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository  extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findBySousCategory(SousCategory sousCategory);
}