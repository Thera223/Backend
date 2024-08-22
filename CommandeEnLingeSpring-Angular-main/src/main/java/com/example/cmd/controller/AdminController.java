package com.example.cmd.controller;

import com.example.cmd.DTO.CategoryDto;
import com.example.cmd.DTO.LivraisonRequest;
import com.example.cmd.DTO.ProduitDto;
import com.example.cmd.model.*;
import com.example.cmd.repository.CategoryRepository;
import com.example.cmd.repository.ProductAttributeRepository;
import com.example.cmd.repository.UtilisateurRepository;
import com.example.cmd.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:8100")
public class AdminController {




    private PasswordEncoder passwordEncoder;

    @Autowired
    FilesStorageService storageService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private FileInfoService fileInfoService;

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

    @PostMapping(path = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Category> createCategory(@RequestParam("files") MultipartFile file, @RequestParam("libelle") String libelle) throws IOException {
        Category category = new Category();
        // récuperer le path du dossier
        Path path_du_dossier = Paths.get("images_du_projet");

        // vérifier si le dossier n'existe pas, le créer.
        if (!Files.exists(path_du_dossier)) {
            Files.createDirectory(path_du_dossier);
        }

        try {
            // créer un path pour le fichier passé en parametre: << MultipartFile file >> en gardant le nom original.

            Path path_du_fichier = path_du_dossier.resolve(file.getOriginalFilename());

            // créer le fichier
            Files.copy(file.getInputStream(), path_du_fichier);

            // Créer un fileInfo
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setUrl(path_du_fichier.toUri().toString());
            // stocker fileInfo dans la base.
            FileInfo fileInfoInDB = this.fileInfoService.creer(fileInfo);
            // lier les fileInfos au category
            category.setLibelle(libelle);
            category.setFileInfo(fileInfoInDB);
        } catch (Exception e) {
            // en cas d'erreur renvoyer le message (lever une exception).
            throw new RuntimeException("Echec. Erreur: " + e.getMessage());
        }

        Category createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> CategoryList = categoryService.getAllCategories();
        return new ResponseEntity<>(CategoryList, HttpStatus.OK);
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

    @PostMapping(path = "/sous-categorie")
    public ResponseEntity<SousCategory> createCategory(@RequestBody SousCategory sousCategory) throws IOException {
        SousCategory sCate = new SousCategory();
        sCate.setCategory(sousCategory.getCategory());
        sCate.setLibelle(sousCategory.getLibelle());
        SousCategory createdsousCategory = sousCategorieService.createSousCategory(sCate);
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

    @PostMapping(path = "/Creerproduit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ajouterProduit(@RequestParam("files") MultipartFile files, Produit produit) throws IOException {
        List<FileInfo> fileInfos = new ArrayList<>();

        // récuperer le path du dossier
        Path path_du_dossier = Paths.get("images_du_projet");

        // vérifier si le dossier n'existe pas, le créer.
        if (!Files.exists(path_du_dossier)) {
            Files.createDirectory(path_du_dossier);
        }

         //parcourrir la liste des fichiers passés en parametre: << MultipartFile[] files >>
        Arrays.asList(files).stream().forEach(file -> {
            try {
                // créer un path pour chaque fichier en gardant le nom original.
                Path path_du_fichier = path_du_dossier.resolve(file.getOriginalFilename());

                // créer le fichier
                Files.copy(file.getInputStream(), path_du_fichier);

                // Créer un fileInfo
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(file.getOriginalFilename());
                fileInfo.setUrl(path_du_fichier.toUri().toString());
                // stocker fileInfo dans la base.
                FileInfo fileInfoInDB = this.fileInfoService.creer(fileInfo);
                fileInfos.add(fileInfoInDB);
            } catch (Exception e) {
                // en cas d'erreur renvoyer le message (lever une exception).
                throw new RuntimeException("Echec. Erreur: " + e.getMessage());
            }
        });

        // lier les fileInfos au produit
        produit.setFileInfo(fileInfos);

        try {
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
    @GetMapping(path = "/listesProduit", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public List<ProduitDto> lireProduits() {
        return produitService.lireProduits();
    }

    //endpoint pour télecharger une image par son nom
    @GetMapping(path = {"/files/{filename:.+}"}, produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping(path = "/lireProduitBySousCategorie/{id}")
    public List<ProduitDto> lireProduitBySousCategorie(@PathVariable long id) {
        return produitService.lireProduitBySousCategorie(id);
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

    @PostMapping("/{id}/variants")
    public ResponseEntity<?> createVariantForSousCategory(
            @PathVariable("id") Long sousCategoryId,
            @RequestBody Map<Long, List<String>> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return ResponseEntity.badRequest().body("Les attributs ne peuvent pas être nuls ou vides.");
        }

        try {
            Map<ProductAttribute, List<String>> attributesAsProductAttributeMap = convertToProductAttributeMap(attributes);
            ProductVariant variant = sousCategorieService.createVariantForSousCategory(sousCategoryId, attributesAsProductAttributeMap);
            return ResponseEntity.ok(variant);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Pour gérer d'autres exceptions potentielles
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création du variant.");
        }
    }

    private Map<ProductAttribute, List<String>> convertToProductAttributeMap(Map<Long, List<String>> attributes) {
        Map<ProductAttribute, List<String>> result = new HashMap<>();

        for (Map.Entry<Long, List<String>> entry : attributes.entrySet()) {
            Long attributeId = entry.getKey();
            List<String> values = entry.getValue();

            if (values == null) {
                values = Collections.emptyList(); // Optionnel : gérer les valeurs nulles en les remplaçant par une liste vide
            }

            ProductAttribute productAttribute = productAttributeRepository.findById(attributeId)
                    .orElseThrow(() -> new NoSuchElementException("Attribut produit non trouvé pour ID : " + attributeId));

            result.put(productAttribute, values);
        }

        return result;
    }
    @GetMapping("/{id}/variant")
    public ResponseEntity<List<ProductVariant>> getVariantsBySousCategory(@PathVariable("id") Long sousCategoryId) {
        try {
            List<ProductVariant> variants = sousCategorieService.getVariantsBySousCategory(sousCategoryId);
            return ResponseEntity.ok(variants);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("produit/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        Produit produit = produitService.getProduitById(id);
        return ResponseEntity.ok(produit);
    }

}






