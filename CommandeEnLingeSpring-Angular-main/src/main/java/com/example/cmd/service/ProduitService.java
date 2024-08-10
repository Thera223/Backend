package com.example.cmd.service;

import com.example.cmd.model.*;
import com.example.cmd.repository.CategoryRepository;
import com.example.cmd.repository.ProduitRepository;
import com.example.cmd.repository.UtilisateurRepository;
import com.example.cmd.repository.VarianteRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProduitService implements ProduitServiceInterface {
    private final ProduitRepository produitRepository;
    private final VarianteRepository varianteRepository;
    private final CategoryRepository categoryRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EntreeSortiService entreeSortiServiceImp;
    private final StockServiceImpl stockService;
    private final HistoriqueService historiqueService;
    private final SousCategorieService sousCategorieService;

    public ProduitService(ProduitRepository produitRepository, VarianteRepository varianteRepository, CategoryRepository categoryRepository, UtilisateurRepository utilisateurRepository, EntreeSortiService entreeSortiServiceImp, StockServiceImpl stockService, HistoriqueService historiqueService, SousCategorieService sousCategorieService) {
        this.produitRepository = produitRepository;
        this.varianteRepository = varianteRepository;
        this.categoryRepository = categoryRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.entreeSortiServiceImp = entreeSortiServiceImp;
        this.stockService = stockService;
        this.historiqueService = historiqueService;
        this.sousCategorieService = sousCategorieService;
    }

    @Override
    @Transactional
    public Object ajouterProduit(Produit produit) {

        Produit produitInDB = this.produitRepository.findByLibelle(produit.getLibelle());
        if (produitInDB != null) {
            EntreeSorti es = new EntreeSorti();
            es.setProduit(produitInDB);
            es.setDate(new Date());
            es.setLibelle("Entrée");
            es.setQuantite(produit.getQuantite());
            entreeSortiServiceImp.creer(es);

            this.stockService.incrementer(produitInDB, produit.getQuantite());
            return "Mise à jour du stock avec succès!";
        }


        Long sousCategoryId = produit.getSousCategory().getId();
        SousCategory categoryOptional = sousCategorieService.getCategory(sousCategoryId);

        if (categoryOptional==null) {
            return "La sous-catégorie du produit n'existe pas.";
        }

        // Associez la catégorie trouvée au produit
        produit.setSousCategory(categoryOptional);

        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "Utilisateur non authentifié.";
        }
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        produit.setUtilisateur(utilisateur);

        // Créer le produit
        Produit p = produitRepository.save(produit);

        // Créer une entrée dans EntreeSorti
        EntreeSorti es = new EntreeSorti();
        es.setProduit(p);
        es.setDate(new Date());
        es.setLibelle("Entrée");
        es.setQuantite(p.getQuantite());
        entreeSortiServiceImp.creer(es);

        // Ajouter au stock
        stockService.ajouterProduit(p);

        // Ajouter un historique
        historiqueService.addCREATIONhistorique(p.getUtilisateur(), "Produit(id:" + p.getId() + ")");

        return "Produit ajouté avec succès!";
    }

    @Override
    @Transactional
    public String modifierProduit(Long id, Produit produitDetails) {
        return produitRepository.findById(id)
                .map(produit -> {
                    produit.setLibelle(produitDetails.getLibelle());
                    produit.setDescription(produitDetails.getDescription());
                    produit.setPrix(produitDetails.getPrix());
                    Produit p = produitRepository.save(produit);

                    // Récupérer l'utilisateur connecté
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication == null || !authentication.isAuthenticated()) {
                        return "Utilisateur non authentifié.";
                    }
                    String username = authentication.getName();
                    Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                    historiqueService.addMODIFICATIONhistorique(utilisateur, "Produit(id:" + p.getId() + ") modifier avec succes");
                    return "Produit modifié avec succès!";
                }).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
    }

    @Override
    @Transactional
    public String ajouterQuantiteProduit(Long id, int quantiteToAdd) {
        return produitRepository.findById(id)
                .map(produit -> {
                    produit.setQuantite(produit.getQuantite() + quantiteToAdd);
                    Produit p =  produitRepository.save(produit);
                    EntreeSorti es = new EntreeSorti();
                    es.setProduit(p);
                    es.setDate(new Date());
                    es.setLibelle("Entrée");
                    es.setQuantite(quantiteToAdd);
                    entreeSortiServiceImp.creer(es);
                    this.stockService.incrementer(p, quantiteToAdd);

                    // Récupérer l'utilisateur connecté
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication == null || !authentication.isAuthenticated()) {
                        return "Utilisateur non authentifié.";
                    }
                    String username = authentication.getName();
                    Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                    historiqueService.addMODIFICATIONhistorique(utilisateur, "Produit(id:" + p.getId() + ")quantite ajouté avec succès");
                    return "Quantité mise à jour avec succès!";
                }).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
    }

    @Override
    @Transactional
    public String supprimerProduit(Long id) {
        // Trouver le produit par son ID
        Optional<Produit> optionalProduit = produitRepository.findById(id);

        if (!optionalProduit.isPresent()) {
            throw new RuntimeException("Produit non trouvé");
        }

        Produit produit = optionalProduit.get();

        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié.");
        }
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Ajouter un historique de suppression
        historiqueService.addSUPPRESSIONhistorique(utilisateur, "Produit(id:" + produit.getId() + ")");

        // Supprimer le produit
        produitRepository.deleteById(id);

        EntreeSorti es = new EntreeSorti();
        es.setProduit(produit);
        es.setDate(new Date());
        es.setLibelle("Sortie");
        es.setQuantite(produit.getQuantite());
        entreeSortiServiceImp.creer(es);
        this.stockService.supprimerProduit(produit);

        return "Produit supprimé avec succès!";
    }


    @Override
    public List<Produit> lireProduits() {
        return produitRepository.findAll();
    }


    @Override
    public Variante ajouterVariante(Long produitId, Variante variante) {
        Produit produit = produitRepository.findById(produitId).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        variante.setProduit(produit);
        return varianteRepository.save(variante);
    }

    @Override
    public List<Variante> lireVariantes(Long produitId) {
//        Produit produit = produitRepository.findById(produitId).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
//        return produit.getVariantes();
        return null;
    }


}
