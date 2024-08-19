package com.example.cmd.repository;

import com.example.cmd.model.Payement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayementRepository extends JpaRepository<Payement, Long> {
    List<Payement> findByCommande_Client_Id(Long clientId);
}
