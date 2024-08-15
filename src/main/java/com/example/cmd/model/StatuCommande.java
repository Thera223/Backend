package com.example.cmd.model;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "StatuCommande")
public class StatuCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
}
