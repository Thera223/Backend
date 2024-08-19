package com.example.cmd.service;

import com.example.cmd.DTO.ProduitDto;
import com.example.cmd.model.Produit;
import com.example.cmd.model.Variante;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ProduitServiceInterface {

    Produit getProduitById(Long id);
    @Transactional
    Object ajouterProduit(Produit produit);

    @Transactional
    String modifierProduit(Long id, Produit produitDetails);

    @Transactional
    String ajouterQuantiteProduit(Long id, int quantiteToAdd);

    Variante ajouterVariante(Long produitId, Variante variante);

    String supprimerProduit(Long id);

    List<ProduitDto> lireProduits();

    List<Variante> lireVariantes(Long produitId);
}
