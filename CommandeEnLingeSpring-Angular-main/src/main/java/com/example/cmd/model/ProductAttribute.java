package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

@Data

@Entity
@Table(name = "ProductAttribute")
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Exemple: Couleur, Taille

}

