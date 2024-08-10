package com.example.cmd.service;

import com.example.cmd.model.Action;

public interface ActionService {
    Action create(Action action);
    Action update(Action action);
    void delete(Action action);
    Action recupererPar(String libelle);
}
