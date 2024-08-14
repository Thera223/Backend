package com.example.cmd.controller;

import com.example.cmd.DTO.LivraisonRequest;
import com.example.cmd.model.*;
import com.example.cmd.repository.CategoryRepository;
import com.example.cmd.repository.UtilisateurRepository;
import com.example.cmd.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    @Autowired

    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private TypeLivraisonService typeLivraisonService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SousCategorieService sousCategorieService;

    @Autowired
    private LivraisonService livraisonService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StatuCommandeService statuCommandeService;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProduitService produitService;
    @Autowired
    private AvisService avisService;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private PayementService payementService;

    @Autowired
    private ClientService clientService;

    // Endpoints pour les rôles

    @GetMapping("/liste_utilisateurs")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        return new ResponseEntity<>(utilisateurRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/checkRole")
    public ResponseEntity<?> checkRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(authentication.getAuthorities());
    }

    @PostMapping("/desactiver-compte/{clientId}")
    public String desactiverCompteClient(@PathVariable Long clientId) {
        utilisateurService.desactiverCompteClient(clientId);
        return "Compte désactivé avec succès.";
    }

    @PostMapping("/roles")
    public ResponseEntity<String> ajouterRole(@RequestBody RoleType roleType) {
        String message = utilisateurService.ajouterRoleType(roleType);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<String> modifierRole(@PathVariable Long id, @RequestBody RoleType roleTypeDetails) {
        String message = utilisateurService.modifierRoleType(id, roleTypeDetails);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<String> supprimerRole(@PathVariable Long id) {
        String message = utilisateurService.supprimerRoleType(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleType>> lireRoles() {
        List<RoleType> roles = utilisateurService.lireRoleTypes();
        return ResponseEntity.ok(roles);
    }

    // Endpoints pour les utilisateurs


    @PostMapping("/creeradmin")
    public ResponseEntity<String> ajouterAdmin(@RequestBody Admin admin, Authentication authentication) {
        String currentUserRole = authentication.getAuthorities().iterator().next().getAuthority();
        if (!currentUserRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas les droits nécessaires pour cette opération.");
        }
        String message = utilisateurService.ajouterAdmin(admin);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/modifieradmin/{id}")
    public ResponseEntity<String> modifierAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        String message = utilisateurService.modifierAdmin(id, admin);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/modifierMotDePasse")
    public ResponseEntity<String> modifierMotDePasse(@RequestBody Map<String, String> requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String nouveauMotDePasse = requestBody.get("nouveauMotDePasse");
            if (nouveauMotDePasse == null || nouveauMotDePasse.isEmpty()) {
                return ResponseEntity.badRequest().body("Le nouveau mot de passe ne peut pas être vide.");
            }
            String message = utilisateurService.modifiermotDePasse(username, nouveauMotDePasse);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Affiche tous les clients

    @GetMapping("/allClients")
    public List<Client> getAllClients() {
        return clientService.obtenirTousLesClients();
    }


    @PostMapping("/Creerpersonnel")
    public ResponseEntity<Personnel> Creerpersonnel(@RequestBody Personnel personnel) {
        try {
            // Récupérer l'authentification pour obtenir l'administrateur courant
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = authentication.getName();
            System.out.println("Nom d'utilisateur authentifié : " + adminUsername);

            // Vérifier que l'utilisateur est un admin
            Admin admin = utilisateurService.findAdminByUsername(adminUsername)
                    .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));

            // Vérifiez que l'email est présent
            if (personnel.getEmail() == null || personnel.getEmail().isEmpty()) {
                throw new IllegalArgumentException("L'email ne peut pas être vide.");
            }

            // Conserver le mot de passe original en clair
            String motDePasseClair = personnel.getMotDePasse();

            // Chiffrez le mot de passe avant de le sauvegarder
            personnel.setMotDePasse(passwordEncoder.encode(motDePasseClair));

            // Assigner l'administrateur au personnel
            personnel.setAdmin(admin);

            // Créer le personnel
            Personnel createdPersonnel = (Personnel) utilisateurService.ajouterPersonnel(personnel);

            System.out.println("Email du personnel créé : " + createdPersonnel.getEmail());

            // Envoi de l'e-mail de confirmation
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(createdPersonnel.getEmail());
            message.setSubject("Compte créé Personnel avec succès");
            message.setText("Bonjour, votre compte a été créé avec succès. Pour des raisons de securités, veuillez modifier votre mot de passe. Vos identifiants sont:\nUsername: " + createdPersonnel.getUsername() + "\nMot de passe: " + motDePasseClair); // Utilisation du mot de passe en clair ici
            mailSender.send(message);

            return ResponseEntity.ok(createdPersonnel);
        } catch (Exception e) {
            // Log l'erreur pour faciliter le débogage
            System.err.println("Erreur lors de la création du personnel et de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/listerUtilisateurs")
    public ResponseEntity<List<Utilisateur>> listUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.listUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }


    @PutMapping("/modifierpersonnel/{id}")
    public ResponseEntity<String> modifierPersonnel(@PathVariable Long id, @RequestBody Personnel personnel) {
        String message = utilisateurService.modifierPersonnel(id, personnel);
        return ResponseEntity.ok(message);
    }



    @DeleteMapping("/supprimerutilisateur/{id}")
    public ResponseEntity<String> supprimerUtilisateur(@PathVariable Long id) {
        String message = utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.ok(message);
    }

    // Endpoints pour les catégories

    @PostMapping("/categories")

    public ResponseEntity<Category> createCategory(@RequestParam("libelle") String libelle) {
        Category createdCategory = categoryService.createCategory(libelle);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        if (category != null) {
            return new ResponseEntity<>(category, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category.getLibelle());
        if (updatedCategory != null) {
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("Catégorie supprimée avec succès!", HttpStatus.OK);
    }

    // Endpoints pour les sous-catégories

    @PostMapping("/sous-categorie")
    public ResponseEntity<SousCategory> createCategory(@RequestBody SousCategory sousCategory) {
        SousCategory createdsousCategory = sousCategorieService.createSousCategory(sousCategory);
        return new ResponseEntity<>(createdsousCategory, HttpStatus.CREATED);
    }

    @GetMapping("/sous-categorie")
    public ResponseEntity<List<SousCategory>> getAllSousCategory() {
        return new ResponseEntity<>(sousCategorieService.getAllSousCategory(), HttpStatus.OK);
    }
    @PostMapping("/souscategoriesAjout/{id}")
    public SousCategory createSousCategoryN(@RequestParam("libelle") String libelle, @PathVariable("id") Long id) {
        return sousCategorieService.createSousCategoryN(libelle, id);
    }

    @GetMapping("/sous-categorie/{id}")
    public ResponseEntity<SousCategory> getSousCategorie(@PathVariable Long id) {
        SousCategory category = sousCategorieService.getCategory(id);
        if (category != null) {
            return new ResponseEntity<>(category, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/categoriesBySous/{id}")
    public List<SousCategory> getCategoryList(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        return sousCategorieService.finBycategorie(category);
    }

    @PutMapping("/sous-categorie/{id}")
    public ResponseEntity<SousCategory> updateSousCategory(@PathVariable Long id, @RequestBody Category category) {
        SousCategory updatedCategory = sousCategorieService.updateCategory(id, category.getLibelle());
        if (updatedCategory != null) {
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/sous-categorie/{id}")
    public ResponseEntity<String> deleteSousCategory(@PathVariable Long id) {
        sousCategorieService.deleteSousCategory(id);
        return new ResponseEntity<>("Catégorie supprimée avec succès!", HttpStatus.OK);
    }

    @PostMapping(value = "/Creerproduit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> ajouterProduit(@RequestBody Produit produit) {
        try {
            // Récupérer l'utilisateur connecté
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié.");
            }

            String username = authentication.getName();
            Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            produit.setUtilisateur(utilisateur);

            // Ajouter le produit
            String resultat = (String) produitService.ajouterProduit(produit);

            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout du produit: " + e.getMessage());
        }
    }

    // Endpoint pour modifier un produit existant
    @PutMapping("/modifierProduit/{id}")
    public String modifierProduit(@PathVariable Long id, @RequestBody Produit produitDetails) {
        return produitService.modifierProduit(id, produitDetails);
    }

    // Endpoint pour ajouter de la quantité à un produit existant
    @PatchMapping("/{id}/AjoutquantiteProduit")
    public String ajouterQuantiteProduit(@PathVariable Long id, @RequestParam int quantiteToAdd) {
        return produitService.ajouterQuantiteProduit(id, quantiteToAdd);
    }

    // Endpoint pour supprimer un produit
    @DeleteMapping("supprimerProduit/{id}")
    public String supprimerProduit(@PathVariable Long id) {
        return produitService.supprimerProduit(id);
    }

    // Endpoint pour obtenir tous les produits
    @GetMapping(path = "/listesProduit")
    public List<Produit> lireProduits() {
        return produitService.lireProduits();
    }

    // Endpoint pour les Avis concernant les produit

    @DeleteMapping("/SupprimerAvis/{id}")
    public ResponseEntity<String> deleteAvis(@PathVariable Long id) {
        String message = avisService.supprimerAvis(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
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

    // Endpoint pour les status concernant les commande
    @PostMapping("/creerStatuCommande")
    public StatuCommande creerStatu(@RequestBody StatuCommande statu) {
        return this.statuCommandeService.creer(statu);
    }

    // Endpoint pour les commandes

    @PutMapping("/changerCommandeStatut/{commandeId}")
    public ResponseEntity<String> changerStatut(@PathVariable Long commandeId, @RequestBody Map<String, String> requestBody) {
        String statutNom = requestBody.get("statutNom");
        if (statutNom == null || statutNom.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Le statut ne peut pas être nul ou vide");
        }
        boolean statutExiste = commandeService.verifierStatutExiste(statutNom);
        if (!statutExiste) {
            return ResponseEntity.badRequest().body("Le statut '" + statutNom + "' n'existe pas");
        }

        String message = commandeService.changerStatut(commandeId, statutNom);
        if (message.contains("non trouvée")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(message);
    }
    @PostMapping("/ajouterTypeLivraison")
    public ResponseEntity<String> ajouterTypeLivraison(@RequestBody TypeLivraison typeLivraison) {
        if (typeLivraison.getLibelle() == null || typeLivraison.getLibelle().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le libellé est requis.");
        }
        if (typeLivraison.getPrix() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le prix doit être supérieur à 0.");
        }
        if (typeLivraison.getDelai() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le délai doit être supérieur à 0.");
        }

        TypeLivraison savedTypeLivraison = typeLivraisonService.ajouterTypeLivraison(typeLivraison);
        return ResponseEntity.status(HttpStatus.CREATED).body("Type de livraison ajouté avec succès ");
    }

    @GetMapping("/listeTypeLivraison")

    public ResponseEntity<List<TypeLivraison>> getAllTypeLivraisons() {
        List<TypeLivraison> typelivraisons = typeLivraisonService.getAllTypeLivraisons();
        return ResponseEntity.ok(typelivraisons);
    }


    @PutMapping("/modifierTypeLivraison/{id}")
    public ResponseEntity<TypeLivraison> mettreAJourTypeLivraison(
            @PathVariable Long id,
            @RequestBody TypeLivraison typeLivraison) {
        try {
            TypeLivraison updatedTypeLivraison = typeLivraisonService.mettreAJourTypeLivraison(id, typeLivraison);
            return ResponseEntity.ok(updatedTypeLivraison);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/supprimerTypeLivraison/{id}")
    public ResponseEntity<String> supprimerTypeLivraison(@PathVariable Long id) {
        try {
            typeLivraisonService.supprimerTypeLivraison(id);
            return ResponseEntity.ok("Type de livraison supprimé.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Type de livraison non trouvé.");
        }
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

    @GetMapping("/listeLivraison")
    public ResponseEntity<List<Livraison>> getAllLivraisons() {
        List<Livraison> livraisons = livraisonService.getAllLivraisons();
        return ResponseEntity.ok(livraisons);
    }

    @GetMapping("/voirCommandes")
    public List<Commande> voirCommandes() {
        return this.commandeService.getCommandes();
    }

    // Endpoint pour payement

    @GetMapping("/voirPayement")
    public List<Payement> voirPayement() {
        return this.payementService.recupererPayements();
    }



}






