package com.example.cmd.controller;
import com.example.cmd.DTO.CreateClientDto;
import com.example.cmd.model.Client;
import com.example.cmd.model.StatusCompte;
import com.example.cmd.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor

@RequestMapping("/creation")

public class CreationCompte {

    @Autowired
    private ClientService clientService;

    @PostMapping("/compteClient")
    public ResponseEntity<?> ajouterClient(@RequestBody CreateClientDto createClientDto) {
        try {
            clientService.ajouterClient(createClientDto);
            return ResponseEntity.ok().build(); // Retourne une réponse HTTP 200 OK si le client est créé avec succès
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retourne une réponse HTTP 400 Bad Request en cas d'erreur
        }
    }

    private Client convertirDtoEnEntite(CreateClientDto dto) {
        Client client = new Client();
        client.setNom(dto.getNom());
        client.setUsername(dto.getUsername());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setMotDePasse(dto.getMotDePasse()); // Assurez-vous de hasher le mot de passe avant de l'enregistrer
        client.setAdresse(dto.getAdresse());
        client.setTelephone(dto.getTelephone());
        client.setStatus(StatusCompte.ACTIVE); // Définissez le statut par défaut comme actif
        return client;
    }


}
