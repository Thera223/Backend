package com.example.cmd.repository;

import com.example.cmd.model.Commande;
import com.example.cmd.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    Facture findByCommande(Commande commande);
}
