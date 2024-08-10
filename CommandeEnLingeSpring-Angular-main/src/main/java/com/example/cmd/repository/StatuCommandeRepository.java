package com.example.cmd.repository;

import com.example.cmd.model.StatuCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatuCommandeRepository extends JpaRepository<StatuCommande, Long> {
    Optional<StatuCommande> findByLibelle(String libelle);
}
