package com.example.cmd.repository;

import com.example.cmd.model.RoleType;
import com.example.cmd.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findById(Long id);
    List<Utilisateur> findByRoleType(RoleType roleType);
    Optional<Utilisateur> findByUsername(String username);

}