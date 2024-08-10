package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class EntreeSorti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String libelle;
    private int quantite;
    @ManyToOne
    private Produit produit;


}
