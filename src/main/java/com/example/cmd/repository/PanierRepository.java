package com.example.cmd.repository;

import com.example.cmd.model.Panier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanierRepository extends JpaRepository<Panier,Long> {
    Panier findByClient_Id(Long clientId);
}
