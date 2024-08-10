package com.example.cmd.service;

import com.example.cmd.model.Action;
import com.example.cmd.repository.ActionRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ActionServiceImpl implements ActionService {
    ActionRepository actionRepository;

    @Override
    public Action create(Action action) {
        return this.actionRepository.save(action);
    }

    @Override
    public Action update(Action action) {
        return create(action);
    }

    @Override
    public void delete(Action action) {
        this.actionRepository.delete(action);
    }

    @Override
    public Action recupererPar(String libelle) {
        return this.actionRepository.findByLibelle(libelle);
    }

    @PostConstruct
    public void init() {
        Action creation = recupererPar("Création");
        Action modification = recupererPar("Modification");
        Action suppression = recupererPar("Suppression");

        if (creation == null) {
            creation = new Action();
            creation.setLibelle("Création");
            this.actionRepository.save(creation);
        }

        if (modification == null) {
            modification = new Action();
            modification.setLibelle("Modification");
            this.actionRepository.save(modification);
        }

        if (suppression == null) {
            suppression = new Action();
            suppression.setLibelle("Suppression");
            this.actionRepository.save(suppression);
        }
    }
}
