package com.example.cmd.repository;

import com.example.cmd.model.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {
    Action findByLibelle(String libelle);
}
