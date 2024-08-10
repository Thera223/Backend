package com.example.cmd.service;

import com.example.cmd.DTO.AvisDTO;
import com.example.cmd.model.Avis;
import com.example.cmd.model.Client;
import com.example.cmd.model.Produit;
import com.example.cmd.repository.AvisRepository;
import com.example.cmd.repository.ClientRepository;
import com.example.cmd.repository.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AvisService {

    @Autowired
    private final AvisRepository avisRepository;
    private final ProduitRepository produitRepository;
    @Autowired
    private final ClientRepository clientRepository;

    public Avis createAvis(AvisDTO avisDTO, Long clientId) {
        Produit produit = produitRepository.findById(avisDTO.getProduitId())
                .orElseThrow(() -> new EntityNotFoundException("Produit not found"));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Avis avis = new Avis();
        avis.setProduit(produit);
        avis.setClient(client);
        avis.setNote(avisDTO.getNote());
        avis.setCommentaire(avisDTO.getCommentaire());
        avis.setDateAvis(LocalDateTime.now());

        return avisRepository.save(avis);
    }

    public List<Avis> lireAvisParProduit(Long produitId) {
        return avisRepository.findByProduitId(produitId);
    }

    public List<Avis> lireAvisParClient(Long clientId) {
        return avisRepository.findByClientId(clientId);
    }

    public Avis modifierAvis(Long id, Avis avisDetails) {
        Avis avis = avisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avis non trouvé avec id : " + id));
        avis.setCommentaire(avisDetails.getCommentaire());
        avis.setNote(avisDetails.getNote());
        return avisRepository.save(avis);
    }

    public String supprimerAvis(Long id) {
        avisRepository.deleteById(id);
        return "Avis supprimé avec succès!";
    }
}
