package com.example.cmd.service;

import com.example.cmd.model.*;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface UtilisateurService {

    Object ajouterPersonnel(Personnel personnel);
    String ajouterAdmin(Admin admin);
    String modifiermotDePasse(String usemane, String NouveaumotDePasse);
    String modifierusername(Long id, String username);

    String modifierAdmin(Long id, Admin adminDetails);

    String modifierPersonnel(Long id, Personnel personnel);


    @Transactional
    List<Utilisateur> listUtilisateurs();

    String supprimerUtilisateur(Long id);

    String assignerCommandeALivreur(Long commandeId, Long livreurId);
    String ajouterRoleType(RoleType roleType);
    String modifierRoleType(Long id, RoleType roleTypeDetails);
    String supprimerRoleType(Long id);

    Client saveClient(Client client);

    List<RoleType> lireRoleTypes();

    void desactiverCompteClient(Long clientId);
    Optional<Admin> findAdminByUsername(String username);

}
