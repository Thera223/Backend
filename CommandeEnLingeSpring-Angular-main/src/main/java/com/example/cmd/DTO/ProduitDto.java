package com.example.cmd.DTO;

import com.example.cmd.model.SousCategory;
import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProduitDto {
    private long id;
    private String libelle;
    private String description;
    private int quantite;
    private double prix;
    private List<String> images = new ArrayList<>();
    private SousCategory sousCategory;
}
