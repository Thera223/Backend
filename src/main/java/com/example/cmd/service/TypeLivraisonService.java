package com.example.cmd.service;

import com.example.cmd.model.Livraison;
import com.example.cmd.model.TypeLivraison;
import com.example.cmd.repository.TypeLivraisonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeLivraisonService {

    @Autowired
    private TypeLivraisonRepository typeLivraisonRepository;


    public TypeLivraison ajouterTypeLivraison(TypeLivraison typeLivraison) {
        return typeLivraisonRepository.save(typeLivraison);
    }

    public TypeLivraisonService(TypeLivraisonRepository typeLivraisonRepository) {
        this.typeLivraisonRepository = typeLivraisonRepository;
    }

    public Optional<TypeLivraison> findById(Long id) {
        return typeLivraisonRepository.findById(id);
    }

    public TypeLivraison mettreAJourTypeLivraison(Long id, TypeLivraison typeLivraison) {
        if (!typeLivraisonRepository.existsById(id)) {
            throw new RuntimeException("TypeLivraison avec l'ID " + id + " n'existe pas.");
        }
        typeLivraison.setId(id);
        return typeLivraisonRepository.save(typeLivraison);
    }

    public void supprimerTypeLivraison(Long id) {
        if (!typeLivraisonRepository.existsById(id)) {
            throw new RuntimeException("TypeLivraison avec l'ID " + id + " n'existe pas.");
        }
        typeLivraisonRepository.deleteById(id);
    }
    public List<TypeLivraison> getAllTypeLivraisons() {
        return typeLivraisonRepository.findAll();
    }
}
