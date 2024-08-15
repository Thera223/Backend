package com.example.cmd.repository;
import com.example.cmd.model.Produit;
import com.example.cmd.model.SousCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProduitRepository extends JpaRepository<Produit, Long> {
    Produit findByLibelle(String libelle);
    List<Produit> findAllBySousCategory(SousCategory sousCategory);
}