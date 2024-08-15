package com.example.cmd.service;

import com.example.cmd.model.Commande;
import com.example.cmd.model.Livraison;
import com.example.cmd.model.TypeLivraison;
import com.example.cmd.repository.LivraisonRepository;
import com.example.cmd.repository.TypeLivraisonRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class LivraisonService {

    @Autowired
    private TypeLivraisonRepository typeLivraisonRepository;

    private LivraisonRepository livraisonRepository;
    public Livraison saveLivraison(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }


    public Optional<TypeLivraison> findById(Long id) {
        return typeLivraisonRepository.findById(id);
    }

    public Livraison getLivraisonById(Long id) {
        return livraisonRepository.findById(id).orElse(null);
    }

    public Optional<Livraison> findByCommande(Commande commande) {
        return livraisonRepository.findByCommande(commande);
    }

    public List<Livraison> getAllLivraisons() {
        return livraisonRepository.findAll();
    }
}

