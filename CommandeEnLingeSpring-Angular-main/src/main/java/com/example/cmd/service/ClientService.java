package com.example.cmd.service;

import com.example.cmd.DTO.ChangePasswordDto;
import com.example.cmd.DTO.CreateClientDto;
import com.example.cmd.model.Client;
import com.example.cmd.model.RoleType;
import com.example.cmd.model.StatusCompte;
import com.example.cmd.repository.ClientRepository;
import com.example.cmd.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public Client convertirDtoEnEntite(CreateClientDto dto) {
        Client client = new Client();
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setUsername(dto.getUsername());
        client.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        client.setAdresse(dto.getAdresse());
        client.setTelephone(dto.getTelephone());
        client.setStatus(StatusCompte.ACTIVE);

        RoleType clientRole = roleRepository.findByNom("CLIENT")
                .orElseGet(() -> roleRepository.save(new RoleType("CLIENT")));
        client.setRoleType(clientRole);

        // Logs pour vérification
        System.out.println("Email: " + client.getEmail());
        System.out.println("Username: " + client.getUsername());
        System.out.println("MotDePasse: " + client.getMotDePasse());

        return client;
    }


    public Client ajouterClient(CreateClientDto dto) {
        Client client = convertirDtoEnEntite(dto);
        return clientRepository.save(client);
    }

    public List<Client> listeClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> trouverClientParId(Long id) {
        return clientRepository.findById(id);
    }

    public boolean estCompteActif(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        return clientOptional.map(client -> client.getStatus() == StatusCompte.ACTIVE).orElse(false);
    }

    public void activerDesactiverCompte(Long clientId, StatusCompte nouveauStatut) {
        Optional<Client> clientOptional = trouverClientParId(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setStatus(nouveauStatut);
            clientRepository.save(client);
        }
    }

    public void changePassword(Long clientId, ChangePasswordDto changePasswordDto) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        optionalClient.ifPresent(client -> {
            if (passwordEncoder.matches(changePasswordDto.getOldPassword(), client.getMotDePasse())) {
                String encodedNewPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());
                client.setMotDePasse(encodedNewPassword);
                clientRepository.save(client);
            } else {
                throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
            }
        });
    }

    public List<Client> obtenirTousLesClients() {
        return clientRepository.findAll();
    }


}
