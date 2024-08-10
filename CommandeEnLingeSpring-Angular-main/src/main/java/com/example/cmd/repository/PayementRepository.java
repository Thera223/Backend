package com.example.cmd.repository;

import com.example.cmd.model.Payement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayementRepository extends JpaRepository<Payement, Long> {
}
