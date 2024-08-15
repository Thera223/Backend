package com.example.cmd.repository;

import com.example.cmd.model.TypeLivraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeLivraisonRepository extends JpaRepository<TypeLivraison, Long> {
    Optional<TypeLivraison> findById(Long id);
}
