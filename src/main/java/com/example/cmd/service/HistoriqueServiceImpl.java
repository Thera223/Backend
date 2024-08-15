package com.example.cmd.service;

import com.example.cmd.model.Action;
import com.example.cmd.model.Historique;
import com.example.cmd.model.Utilisateur;
import com.example.cmd.repository.HistoriqueRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class HistoriqueServiceImpl implements HistoriqueService{

    @Autowired
    private HistoriqueRepository historiqueRepository;

    private ActionServiceImpl actionService;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public Historique addCREATIONhistorique(Utilisateur utilisateur, String description) {
        Historique historique = new Historique();
        Action creation = this.actionService.recupererPar("Création");
        System.out.println(creation.toString());
        historique.set_action(creation);
        historique.set_description("L'entité affectée est: "+description);
        return this.historiqueRepository.save(historique);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Historique addMODIFICATIONhistorique(Utilisateur utilisateur, String description) {
        Historique historique = new Historique();
        Action modification = this.actionService.recupererPar("Modification");
        historique.set_action(modification);
        historique.set_description("L'entité affectée est: "+description);
        return this.historiqueRepository.save(historique);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Historique addSUPPRESSIONhistorique(Utilisateur utilisateur, String description) {
        Historique historique = new Historique();
        Action suppression = this.actionService.recupererPar("Suppression");
        historique.set_action(suppression);
        historique.set_description("L'entité affectée est: "+description);
        return this.historiqueRepository.save(historique);
    }

    @Override
    public Historique createHistorique(Historique historique) {
        return null;
    }

    @Override
    public List<Historique> voirToutesHistoriques() {
        return this.historiqueRepository.findAll();
    }

    @Override
    public List<Historique> voirToutesHistoriquesPar(Utilisateur utilisateur) {
        return this.historiqueRepository.findAllBy_faitPar(utilisateur);
    }
}
