package com.example.cmd.controller;

import com.example.cmd.DTO.*;
import com.example.cmd.config.CustomUserPrincipal;
import com.example.cmd.model.*;
import com.example.cmd.service.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/client")
@CrossOrigin(origins = "http://localhost:8100")
public class ClientController {
    @Autowired
    private StatuCommandeService statuCommandeService;

    @Autowired
    private LivraisonService livraisonService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private SousCategorieService sousCategorieService;

    @Autowired
    private PayementService payementService;


    @Autowired
    private ClientService clientService;

    @Autowired
    private ProduitService produitService;
    @Autowired
    private PanierService panierService;

    @Autowired
    private AvisService avisService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private RecuService recuService;
    @Autowired
    private TypeLivraisonService typeLivraisonService;

    public ClientController(ClientService clientService, AvisService avisService, PanierService panierService) {
        this.clientService = clientService;
        this.avisService = avisService;
        this.panierService = panierService;
    }

    // Endpoint pour récupérer tous les reçus
    @GetMapping("/recus")
    public ResponseEntity<List<Recu>> getAllRecus() {
        List<Recu> recus = recuService.recupererRecus();
        return new ResponseEntity<>(recus, HttpStatus.OK);
    }

    @GetMapping("/commande")
    public ResponseEntity<List<Commande>> getCommandes(){
        return new ResponseEntity<>(commandeService.getCommandes(), HttpStatus.OK);
    }
    // Endpoint pour récupérer un reçu par ID
    @GetMapping("/recus/{id}")
    public ResponseEntity<Recu> getRecuById(@PathVariable Long id) {
        Optional<Recu> recu = recuService.recupererRecuParId(id);
        return recu.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // Récupère la liste des avis associés à un produit spécifié

    @GetMapping("/Avisbyproduit/{produitId}")
    public ResponseEntity<List<Avis>> getAvisByProduit(@PathVariable Long produitId) {
        List<Avis> avis = avisService.lireAvisParProduit(produitId);
        return new ResponseEntity<>(avis, HttpStatus.OK);
    }

    //Récupère la liste des avis associés à un client

    @GetMapping("/Avisbyclient/{clientId}")
    public ResponseEntity<List<Avis>> getAvisByClient(@PathVariable Long clientId) {
        List<Avis> avis = avisService.lireAvisParClient(clientId);
        return new ResponseEntity<>(avis, HttpStatus.OK);
    }

    @GetMapping("/{clientId}/profil")
    public Object afficherProfil(@PathVariable Long clientId) {
        if (!clientService.estCompteActif(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé : Compte désactivé.");
        }
        return "Votre compte est activé";
    }

    @GetMapping("/TypeLivraison")
    public ResponseEntity<List<TypeLivraison>> getTypeLivraison() {
        return new ResponseEntity<>(typeLivraisonService.getAllTypeLivraisons(), HttpStatus.OK);
    }
    @PostMapping("/{clientId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long clientId, @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            clientService.changePassword(clientId, changePasswordDto);
            return ResponseEntity.ok().build(); // Retourne une réponse HTTP 200 OK si le changement de mot de passe réussit
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retourne une réponse HTTP 400 Bad Request si l'ancien mot de passe est incorrect
        }
    }

    private Client convertirDtoEnEntite(CreateClientDto dto) {
        Client client = new Client();
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setMotDePasse(dto.getMotDePasse()); // Assurez-vous de hasher le mot de passe avant de l'enregistrer
        client.setAdresse(dto.getAdresse());
        client.setTelephone(dto.getTelephone());
        client.setStatus(StatusCompte.ACTIVE); // Définissez le statut par défaut comme actif
        return client;
    }

    // Endpoint pour les Avis

    @PostMapping("/CreerAvis")
    public ResponseEntity<Avis> createAvis(@RequestBody AvisDTO avisDTO) {
        Long clientId = getAuthenticatedClientId();
        Avis createdAvis = avisService.createAvis(avisDTO, clientId);
        return new ResponseEntity<>(createdAvis, HttpStatus.CREATED);
    }

    private Long getAuthenticatedClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserPrincipal) {
                CustomUserPrincipal customPrincipal = (CustomUserPrincipal) principal;
                if (customPrincipal.getUtilisateur() != null) {
                    return customPrincipal.getUtilisateur().getId();
                } else {
                    throw new IllegalArgumentException("Authenticated user is not a client");
                }
            }
            throw new IllegalArgumentException("Principal is not an instance of CustomUserPrincipal");
        }
        throw new IllegalArgumentException("User is not authenticated");
    }

    @PutMapping("/modifierAvis/{id}")
    public ResponseEntity<Avis> updateAvis(@PathVariable Long id, @RequestBody Avis avisDetails) {
        Avis updatedAvis = avisService.modifierAvis(id, avisDetails);
        return new ResponseEntity<>(updatedAvis, HttpStatus.OK);
    }

    @DeleteMapping("/SupprimerAvis/{id}")
    public ResponseEntity<String> deleteAvis(@PathVariable Long id) {
        String message = avisService.supprimerAvis(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // Ajouter le prodit dans le panier
    @PostMapping("/{clientId}/panier/ajouterProduit")
    public ResponseEntity<Panier> ajouterProduitAuPanier(@PathVariable Long clientId, @RequestParam Long produitId, @RequestParam int quantite) {
        Panier panier = panierService.ajouterProduitAuPanier(clientId, produitId, quantite);
        return new ResponseEntity<>(panier, HttpStatus.OK);
    }


    // Modifier la quantité du produit dans le panier
    @PutMapping("/{clientId}/panier/{panierId}/modifierQuantite")
    public ResponseEntity<Panier> modifierQuantiteProduitDansPanier(@PathVariable Long clientId, @PathVariable Long panierId, @RequestParam Long produitId, @RequestParam int nouvelleQuantite) {
        Panier panier = panierService.modifierQuantiteProduitDansPanier(panierId, produitId, nouvelleQuantite);
        return new ResponseEntity<>(panier, HttpStatus.OK);
    }

    // Supprimer le produit dans le panier
    @DeleteMapping("/{clientId}/panier/{panierId}/supprimerProduit")
    public ResponseEntity<Panier> supprimerProduitDuPanier(@PathVariable Long clientId, @PathVariable Long panierId, @RequestParam Long produitId) {
        Panier panier = panierService.supprimerProduitDuPanier(panierId, produitId);
        return new ResponseEntity<>(panier, HttpStatus.OK);
    }

    // Supprimer son panier
    @DeleteMapping("/{clientId}/panier/{panierId}")
    public ResponseEntity<String> supprimerPanier(@PathVariable Long clientId, @PathVariable Long panierId) {
        String message = panierService.supprimerPanier(panierId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }



    // Afficher tous les produits dans le Panier

    @GetMapping("/{panierId}/produits")
    public ResponseEntity<List<ProduitCommandee>> getAllProduitsInPanier(@PathVariable Long panierId) {
        List<ProduitCommandee> produits = panierService.getAllProduitsInPanier(panierId);
        return new ResponseEntity<>(produits, HttpStatus.OK);
    }


    // Afficher un produit dans le panier par son ID

    @GetMapping("/{panierId}/produits/{produitId}")
    public ResponseEntity<Produit> getProduitByIdInPanier(@PathVariable Long panierId, @PathVariable Long produitId) {
        Produit produit = panierService.getProduitByIdInPanier(panierId, produitId);
        if (produit != null) {
            return new ResponseEntity<>(produit, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Payé tous les produits dans le panier
    @PostMapping("/payer/panier/{panierId}")
    public ResponseEntity<String> payerProduitsDansPanier(@PathVariable Long panierId, @RequestParam float montantClient) {
        String resultat = panierService.payerProduitsDansPanier(panierId, montantClient);
        return new ResponseEntity<>(resultat, HttpStatus.OK);
    }

    @PostMapping("/passerCommandeViaPanier/{id_panier}")
    public ResponseEntity<Map<String, String>> passerCommandeViaPanier(@PathVariable Long id_panier) {
        this.commandeService.passerCommandeViaPanier(id_panier);

        Map<String, String> response = new HashMap<>();
        response.put("message", "CommandeViaPanier");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint pour obtenir tous les produits
    @GetMapping(path = "/listesProduit", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public List<ProduitDto> lireProduits() {
        return produitService.lireProduits();
    }

    @PostMapping("/passerCommande")
    public ResponseEntity<Commande> passerCommandes(
            @RequestBody List<ProduitCommandee> produits) {

        // Récupérer l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Trouver le client par son nom d'utilisateur
        Client client = clientService.findByUsername(username);
        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Passer la commande avec les produits
        Commande c = commandeService.passerCommande(produits);

        // Associer le client à la commande
        c.setClient(client);

        // Sauvegarder la commande dans la base de données
        commandeService.saveCommande(c);

        return new ResponseEntity<>(c, HttpStatus.CREATED);
    }
    @GetMapping("/voirCommandes")
    public List<Commande> voirCommandes() {
        return this.commandeService.getCommandes();
    }


    @PostMapping("/effectuerPayement")
    public Payement effectuerPayement(@RequestBody Commande commande) {
        return this.payementService.effectuerPayement(commande);
    }

    @GetMapping("/sous-categorie")
    public ResponseEntity<List<SousCategory>> getAllSousCategory() {
        return new ResponseEntity<>(sousCategorieService.getAllSousCategory(), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }



    // Endpoint pour récupérer les commandes d'un client spécifique
    @GetMapping("/commandes/{clientId}")
    public ResponseEntity<List<Commande>> getCommandesByClientId(@PathVariable Long clientId) {
        List<Commande> commandes = commandeService.getCommandesByClientId(clientId);
        if (commandes.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(commandes);
        }
    }

    // Endpoint pour récupérer les payements d'un client spécifique
    @GetMapping("/paiements/client/{clientId}")
    public ResponseEntity<List<Payement>> getPayementsByClient(@PathVariable Long clientId) {
        List<Payement> paiements = payementService.getPayementsByClientId(clientId);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("produit/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        Produit produit = produitService.getProduitById(id);
        return ResponseEntity.ok(produit);
    }
    @PostMapping("/ajouterLivraison/{commandeId}")
    @Transactional
    public ResponseEntity<String> ajouterLivraison(
            @PathVariable Long commandeId,
            @RequestBody LivraisonRequest request) {

        // Vérifier l'existence de la commande
        Optional<Commande> optionalCommande = commandeService.findById(commandeId);
        if (optionalCommande.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Commande avec ID " + commandeId + " non trouvée.");
        }

        // Vérifier l'existence du type de livraison par ID
        Optional<TypeLivraison> optionalTypeLivraison = livraisonService.findById(request.getTypeLivraisonId());
        if (optionalTypeLivraison.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Type de livraison avec ID " + request.getTypeLivraisonId() + " non trouvé.");
        }

        Commande commande = optionalCommande.get();
        if (livraisonService.findByCommande(commande).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Une livraison est déjà associée à cette commande.");
        }

        // Créer et sauvegarder la livraison
        Livraison livraison = new Livraison();
        livraison.setLieuLivraison(request.getLieuLivraison());
        livraison.setTypeLivraison(optionalTypeLivraison.get());
        livraison.setCommande(commande);

        Livraison savedLivraison = livraisonService.saveLivraison(livraison);

        // Mettre à jour le statut de la commande
        StatuCommande statutEnCours = statuCommandeService.recupererStatusCommande("en_cours");
        commande.setStatu(statutEnCours);

        commandeService.saveCommande(commande);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Livraison ajoutée et statut mis à jour à 'en_cours'.");
    }




}


