package com.example.cmd.repository;

import com.example.cmd.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleType, Long> {
    Optional<RoleType> findByNom(String nom);
}

