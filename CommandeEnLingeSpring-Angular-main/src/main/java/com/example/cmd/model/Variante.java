package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "VARIANTE")

public class Variante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String couleur;
    private String taille;
    private Float prix;
    private int quantite;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    public Variante(String couleur, String taille, Float prix, int quantite, Produit produit) {
        this.couleur = couleur;
        this.taille = taille;
        this.prix = prix;
        this.quantite = quantite;
        this.produit = produit;
    }

    public Variante() {

    }
}
