package com.example.cmd.service;

import com.example.cmd.model.ProduitCommandee;
import com.example.cmd.repository.ProduitCommandeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduitCommandeeService {
    @Autowired
    private ProduitCommandeeRepository produitCommandeeRepository;

    public List<ProduitCommandee> getAllProduitCommandee(){
        return this.produitCommandeeRepository.findAll();
    }

    public ProduitCommandee creer(ProduitCommandee produitCommandee){
        return this.produitCommandeeRepository.save(produitCommandee);
    }
}
