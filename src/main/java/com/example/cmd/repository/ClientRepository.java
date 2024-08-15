package com.example.cmd.repository;

import com.example.cmd.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByUsername(String username);
}
