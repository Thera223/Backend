package com.example.cmd.service;

import com.example.cmd.model.*;
import com.example.cmd.repository.FactureRepository;
import com.example.cmd.repository.PayementRepository;
import com.example.cmd.repository.ProduitRepository;
import com.example.cmd.repository.RecuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class PayementService {
    private PayementRepository payementRepository;
    private RecuRepository recuRepository;
    private CommandeService commandeService;
    private FactureRepository factureRepository;
    private StockServiceImpl stockService;
    private EntreeSortiServiceImp entreeSortiService;

    @Transactional
    public Payement effectuerPayement(Commande commande) {
        Commande commande1 = this.commandeService.getCommande(commande.getId());

        // Créer des sortis
        System.out.println("Créer des sortis");
        List<ProduitCommandee> produitCommandeeList = commande1.getProduitCommandees();
        for (ProduitCommandee pCommandee : produitCommandeeList) {
            EntreeSorti es = new EntreeSorti();
            es.setProduit(pCommandee.getProduit());
            es.setDate(new Date());
            es.setLibelle("Sortie");
            es.setQuantite(pCommandee.getQuantite());
            this.entreeSortiService.creer(es);
        }
        // Retirer quantité de chaque produit dans le stock
        commande1.getProduitCommandees().forEach(produitCommandee -> this.stockService.retirerProduit(produitCommandee.getProduit(), produitCommandee.getQuantite()));

        Payement payement = new Payement();
        payement.setCommande(commande1);
        Facture facture = this.factureRepository.findByCommande(commande1);
        if (facture == null) {
            throw new RuntimeException("facture non trouvable");
        }
        payement.setMontant(facture.getTotal());
        Payement payementSaved = this.payementRepository.save(payement);
        Recu recu = new Recu();
        recu.setPayement(payementSaved);
        recu.setTotal(facture.getTotal());
        this.recuRepository.save(recu);
        return payementSaved;
    }

    public List<Payement> recupererPayements() {
        return this.payementRepository.findAll();
    }
}
