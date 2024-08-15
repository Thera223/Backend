package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ProduitCommandee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Produit produit;
    private int quantite;
}
