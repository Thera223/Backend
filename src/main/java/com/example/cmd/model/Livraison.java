package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name="livraison")
@Entity
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lieuLivraison;

    @ManyToOne
    @JoinColumn(name = "type_livraison_id")
    private TypeLivraison typeLivraison;

    @OneToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;


}
