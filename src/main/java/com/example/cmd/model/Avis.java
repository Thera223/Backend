package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateAvis;
    private String commentaire;
    private int note; // Par exemple, de 1 à 5 étoiles

    @ManyToOne
    private Client client;

    @ManyToOne
    private Produit produit;

}
