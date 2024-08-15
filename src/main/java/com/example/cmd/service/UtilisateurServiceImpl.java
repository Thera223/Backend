package com.example.cmd.service;

import com.example.cmd.model.*;
import com.example.cmd.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProduitRepository produitRepository;
    private final RoleRepository roleRepository;
    private final HistoriqueServiceImpl historiqueService;
    private  final StockServiceImpl stockService;
    private EntreeSortiServiceImp entreeSortiServiceImp;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;  // Injectez le ClientService
    private StockRepository stockRepository;
    private CategoryRepository categoryRepository;
    private JavaMailSender mailSender;


    @Override
    public String ajouterRoleType(RoleType roleType) {
        roleRepository.save(roleType);
        return "Role ajouté avec succès!";
    }

    @Override
    public String modifierRoleType(Long id, RoleType roleTypeDetails) {
        return roleRepository.findById(id)
                .map(roleType -> {
                    roleType.setNom(roleTypeDetails.getNom());
                    roleRepository.save(roleType);
                    return "Role modifié avec succès!";
                }).orElseThrow(() -> new RuntimeException("Role n'existe pas"));
    }

    @Override
    public String supprimerRoleType(Long id) {
        Optional<RoleType> roleTypeOptional = roleRepository.findById(id);

        if (roleTypeOptional.isPresent()) {
            roleRepository.deleteById(id);
            return "Role supprimé avec succès!";
        } else {
            return "Aucun role trouvé avec l'id fourni.";
        }
    }


    @Override
    public Client saveClient(Client client) {
        client.setMotDePasse(passwordEncoder.encode(client.getMotDePasse()));
        return clientRepository.save(client);
    }

    @Override
    public List<RoleType> lireRoleTypes() {
        return List.of();
    }

    @Override
    @Transactional
    public Personnel ajouterPersonnel(Personnel personnel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String adminUsername = authentication.getName();
        Utilisateur admin = utilisateurRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec le nom d'utilisateur : " + adminUsername));

        // Vérifier que l'email et le mot de passe sont non nuls et non vides
        if (personnel.getEmail() == null || personnel.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide.");
        }
        if (personnel.getMotDePasse() == null || personnel.getMotDePasse().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }

        try {
            // Assigner le rôle par défaut
            RoleType personnelRole = roleRepository.findByNom("PERSONNEL")
                    .orElseGet(() -> roleRepository.save(new RoleType("PERSONNEL")));
            personnel.setRoleType(personnelRole);

            // Cryptage du mot de passe
            personnel.setMotDePasse(passwordEncoder.encode(personnel.getMotDePasse()));

            // Assigner l'administrateur courant
            personnel.setAdmin((Admin) admin);
            Personnel perso = utilisateurRepository.save(personnel);
            historiqueService.addCREATIONhistorique(perso.getAdmin(), "Personnel(id:" + perso.getId() + ")");

            // Sauvegarder le personnel
            return perso;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du personnel : " + e.getMessage());
        }
    }


    @Override
    public String modifierPersonnel(Long id, Personnel personnelDetails) {
        return utilisateurRepository.findById(id)
                .map(personnel -> {
                    personnel.setUsername(personnelDetails.getUsername());
                    personnel.setMotDePasse(passwordEncoder.encode(personnelDetails.getMotDePasse()));
                    utilisateurRepository.save(personnel);
                    return "Personnel modifié avec succès!";
                }).orElseThrow(() -> new RuntimeException("Personnel n'existe pas"));
    }

    @Override
    public List<Utilisateur> listUtilisateurs() {
            return utilisateurRepository.findAll();
        }

    @Transactional
    @Override
    public String supprimerUtilisateur(Long id) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(id);

        if (utilisateurOptional.isPresent()) {
            utilisateurRepository.deleteById(id);
            return "Utilisateur supprimé avec succès!";
        } else {
            return "Aucun utilisateur trouvé avec l'id fourni.";
        }
    }

    public void desactiverCompteClient(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        clientOptional.ifPresent(client -> {
            client.setStatus(StatusCompte.DESACTIVE);
            clientRepository.save(client);
        });
    }

    @Override
    public Optional<Admin> findAdminByUsername(String username) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByUsername(username);
        if (utilisateur.isPresent() && utilisateur.get() instanceof Admin) {
            return Optional.of((Admin) utilisateur.get());
        }
        return Optional.empty();
    }

    @Override
    public String ajouterAdmin(Admin admin) {
        RoleType adminRole = roleRepository.findByNom("ADMIN")
                .orElseGet(() -> roleRepository.save(new RoleType("ADMIN")));
        admin.setRoleType(adminRole);

        // Conserver le mot de passe en clair pour l'email
        String motDePasseClair = admin.getMotDePasse();

        // Chiffrer le mot de passe avant de sauvegarder
        admin.setMotDePasse(passwordEncoder.encode(motDePasseClair));
        utilisateurRepository.save(admin);
        // Envoi de l'e-mail de confirmation
        envoyerEmailConfirmation(admin, motDePasseClair);

        return "Nouvel admin ajouté avec succès!";
    }

    private void envoyerEmailConfirmation(Admin admin, String motDePasseClair) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(admin.getEmail());
        message.setSubject("Compte Admin créé avec succès");
        message.setText("Bonjour, votre compte administrateur a été créé avec succès. " +
                "Veuillez modifier votre mot de passe pour des raisons de sécurité. Vos identifiants sont:\n" +
                "Username: " + admin.getUsername() + "\nMot de passe: " + motDePasseClair);
        mailSender.send(message);
    }

    @Override
    public String modifiermotDePasse(String username, String NouveaumotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals(username)) {
            throw new IllegalStateException("Vous n'êtes pas autorisé à modifier ce mot de passe");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(NouveaumotDePasse));
        utilisateurRepository.save(utilisateur);

        return "Mot de passe mis à jour avec succès pour l'utilisateur: " + username;
    }

    @Override
    public String modifierusername(Long id, String username) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setUsername(username);
                    utilisateurRepository.save(utilisateur);
                    return "Username modifié avec succès!";
                }).orElseThrow(() -> new RuntimeException("Utilisateur n'existe pas"));
    }

    @Override
    public String modifierAdmin(Long id, Admin adminDetails) {
        Admin admin = (Admin) utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec id : " + id));

        admin.setUsername(adminDetails.getUsername());
        admin.setMotDePasse(passwordEncoder.encode(adminDetails.getMotDePasse()));
        utilisateurRepository.save(admin);
        return "Admin modifié avec succès!";
    }

    @Override
    public String assignerCommandeALivreur(Long commandeId, Long livreurId) {
        return "";
    }

    @PostConstruct
    public void initAdmin() {
        try {
            RoleType adminRole = roleRepository.findByNom("ADMIN")
                    .orElseGet(() -> roleRepository.save(new RoleType("ADMIN")));

            List<Utilisateur> admins = utilisateurRepository.findByRoleType(adminRole);

            if (admins.isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername("samake");
                admin.setMotDePasse(passwordEncoder.encode("samake"));
                admin.setRoleType(adminRole);
                admin.setEmail("email@example.com");
                Utilisateur savedAdmin = utilisateurRepository.save(admin);

                System.out.println("Admin créé avec succès. ID: " + savedAdmin.getId());
            } else {
                System.out.println("Un administrateur existe déjà.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de l'admin : " + e.getMessage());
        }
    }
}
