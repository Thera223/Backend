package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "type_livraison")
public class TypeLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
    private double prix;
    private int delai;

}
