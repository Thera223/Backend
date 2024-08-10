package com.example.cmd.service;

import com.example.cmd.model.EntreeSorti;
import com.example.cmd.repository.EntreeSortiRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EntreeSortiServiceImp implements EntreeSortiService{
    private EntreeSortiRepository entreeSortiRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public EntreeSorti creer(EntreeSorti entreeSorti) {
        return this.entreeSortiRepository.save(entreeSorti);
    }

    @Override
    public List<EntreeSorti> voir_EntreeSorti() {
        return this.entreeSortiRepository.findAll();
    }
}