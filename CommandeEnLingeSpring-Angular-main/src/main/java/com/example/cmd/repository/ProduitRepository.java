package com.example.cmd.repository;
import com.example.cmd.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProduitRepository extends JpaRepository<Produit, Long> {
    Produit findByLibelle(String libelle);
}