package com.example.cmd.repository;

import com.example.cmd.model.Variante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VarianteRepository extends JpaRepository<Variante, Long> {
}