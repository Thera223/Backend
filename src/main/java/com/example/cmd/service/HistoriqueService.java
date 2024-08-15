package com.example.cmd.service;

import com.example.cmd.model.Historique;
import com.example.cmd.model.Utilisateur;
import jakarta.transaction.Transactional;

import java.util.List;

public interface HistoriqueService {
    @Transactional(Transactional.TxType.REQUIRED)
    Historique addCREATIONhistorique(Utilisateur utilisateur, String description);

    @Transactional(Transactional.TxType.REQUIRED)
    Historique addMODIFICATIONhistorique(Utilisateur utilisateur, String description);

    @Transactional(Transactional.TxType.REQUIRED)
    Historique addSUPPRESSIONhistorique(Utilisateur utilisateur, String description);

    Historique createHistorique(Historique historique);
    List<Historique> voirToutesHistoriques();
    List<Historique> voirToutesHistoriquesPar(Utilisateur utilisateur);
}
