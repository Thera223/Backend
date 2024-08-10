package com.example.cmd.repository;

import com.example.cmd.model.Commande;
import com.example.cmd.model.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    Optional<Livraison> findByCommande(Commande commande);
}
