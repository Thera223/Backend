package com.example.cmd.repository;

import com.example.cmd.model.Produit;
import com.example.cmd.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProduit(Produit produit);
}
