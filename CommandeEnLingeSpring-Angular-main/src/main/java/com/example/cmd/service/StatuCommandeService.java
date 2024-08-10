package com.example.cmd.service;

import com.example.cmd.model.StatuCommande;
import com.example.cmd.repository.StatuCommandeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class StatuCommandeService {
    private StatuCommandeRepository statuCommandeRepository;


    public StatuCommande recupererStatusCommande(String libelle) {
        return statuCommandeRepository.findByLibelle(libelle)
                .orElseThrow(() -> new RuntimeException("Statut '" + libelle + "' non trouvé"));
    }

    public Optional<StatuCommande> findById(Long id) {
        return statuCommandeRepository.findById(id);
    }

    public StatuCommande creer(StatuCommande statu) {
        return this.statuCommandeRepository.save(statu);
    }

    public StatuCommande modifier(long id, StatuCommande statu){
        StatuCommande statuCommande = this.statuCommandeRepository.findById(id).orElse(null);
        if(statuCommande != null) {
            statuCommande.setLibelle(statu.getLibelle());
            return this.creer(statuCommande);
        }
        return null;
    }

    public void supprimer(long id) {
        this.statuCommandeRepository.deleteById(id);
    }
}
