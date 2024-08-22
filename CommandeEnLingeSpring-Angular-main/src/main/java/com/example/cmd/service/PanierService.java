package com.example.cmd.service;

import com.example.cmd.model.*;
import com.example.cmd.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PanierService {

    private final PanierRepository panierRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final FactureRepository factureRepository;
    private final ProduitCommandeeService produitCommandeeService;
    private StatuCommandeService statuCommandeService;

    public String supprimerPanier(Long id) {
        panierRepository.deleteById(id);
        return "Panier supprimé avec succès!";
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Panier ajouterProduitAuPanier(Long clientId, Long produitId, int quantite) {
        Panier panier = panierRepository.findByClient_Id(clientId);
        if (panier == null) {
            panier = new Panier();
            panier.setClient(clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client non trouvé")));
            panier = panierRepository.save(panier);
        }

        Produit produit = produitRepository.findById(produitId).orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        ProduitCommandee produitCommandee = new ProduitCommandee();
        produitCommandee.setProduit(produit);
        produitCommandee.setQuantite(quantite);
        ProduitCommandee produitCommandeeInDB = produitCommandeeService.creer(produitCommandee);
        panier.getProduits().add(produitCommandeeInDB);
        panier.setTotal(panier.getTotal());
        panierRepository.save(panier);
        return panier;
    }

    public Panier modifierQuantiteProduitDansPanier(Long panierId, Long produitId, int nouvelleQuantite) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new IllegalArgumentException("Panier non trouvé avec id : " + panierId));
        panier.modifierQuantiteProduit(produitId, nouvelleQuantite);
        return panierRepository.save(panier);
    }

    @Transactional
    public Panier supprimerProduitDuPanier(Long panierId, Long produitId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new IllegalArgumentException("Panier non trouvé avec id : " + panierId));
        panier.supprimerProduit(produitId);
        return panierRepository.save(panier);
    }

    @Transactional
    public String payerProduitsDansPanier(Long panierId, float montantClient) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new IllegalArgumentException("Panier non trouvé avec id : " + panierId));

        if (montantClient < panier.getTotal()) {
            return "Montant insuffisant pour effectuer le paiement.";
        }

        float montantRestant = montantClient - panier.getTotal();

        for (ProduitCommandee produit : panier.getProduits()) {
            Produit p = produitRepository.findById(produit.getId()).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            if (p.getQuantite() < produit.getQuantite()) {
                return "Quantité insuffisante pour le produit : " + produit.getId();
            }
            p.setQuantite(p.getQuantite() - produit.getQuantite());
            produitRepository.save(p);
        }

        Commande commande = new Commande();
        //commande.setProduits(new ArrayList<>());
        commandeRepository.save(commande);

        Facture facture = new Facture();
        facture.setTotal(panier.getTotal());
        facture.setCommande(commande);
        factureRepository.save(facture);

        panier.getProduits().clear();
        panierRepository.save(panier);

        return "Paiement effectué avec succès. Montant restant à retourner : " + montantRestant;
    }

    public Panier getPanierById(Long panierId) {
        return panierRepository.findById(panierId).orElse(null);
    }

    public List<ProduitCommandee> getAllProduitsInPanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId).orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        List<ProduitCommandee> produitCommandeeList = new ArrayList<>();
        for (ProduitCommandee produitCommandee : panier.getProduits()) {
            List<FileInfo> fileInfos = new ArrayList<>();
            fileInfos = produitCommandee.getProduit().getFileInfo();
            for (FileInfo fileInfo : fileInfos) {
                String imagePath = String.format("http://localhost:8080/admin/files/"+fileInfo.getName());
                fileInfo.setUrl(imagePath);
            }
            produitCommandee.getProduit().setFileInfo(fileInfos);
            produitCommandeeList.add(produitCommandee);
        }
        return produitCommandeeList;
    }

    public Produit getProduitByIdInPanier(Long panierId, Long produitId) {
        Panier panier = panierRepository.findById(panierId).orElse(null);
        if (panier == null) {
            throw new RuntimeException("Panier non trouvé");
        }
        return panier.getProduits().stream()
                .filter(produit -> produit.getProduit().getId().equals(produitId))
                .findFirst()
                .orElse(null).getProduit();
    }

    public List<Produit> getAllProduitsInPanierForClient(Long clientId) {
        // Récupérer le panier du client à partir de son ID
        Panier panier = panierRepository.findByClient_Id(clientId);

        // Si le panier n'existe pas, lancer une exception
        if (panier == null) {
            throw new RuntimeException("Panier non trouvé pour le client avec ID : " + clientId);
        }

        // Retourner la liste des produits dans le panier
        return panier.getProduits();
    }


}
