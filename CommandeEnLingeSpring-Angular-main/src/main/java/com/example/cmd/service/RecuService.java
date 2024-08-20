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
    private RecuRepository recuRepository;
    public List<Recu> recupererRecus() {
        return recuRepository.findAll();
    }

    public Optional<Recu> recupererRecuParId(Long id) {
        return recuRepository.findById(id);
    }
}
