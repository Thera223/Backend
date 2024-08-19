package com.example.cmd.service;

import com.example.cmd.model.Recu;
import com.example.cmd.repository.RecuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecuService {
    private final RecuRepository recuRepository;

    // Récupérer tous les reçus
    public List<Recu> recupererRecus() {
        return recuRepository.findAll();
    }

    // Récupérer un reçu par son ID
    public Optional<Recu> recupererRecuParId(Long id) {
        return recuRepository.findById(id);
    }
}
