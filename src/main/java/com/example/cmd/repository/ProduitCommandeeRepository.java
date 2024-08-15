package com.example.cmd.repository;

import com.example.cmd.model.ProduitCommandee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitCommandeeRepository extends JpaRepository<ProduitCommandee, Long> {
}
