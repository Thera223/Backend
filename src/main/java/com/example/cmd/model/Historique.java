package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Historique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Action _action;
    private String _description;
    private Date _date = new Date();
    @ManyToOne
    private Utilisateur _faitPar;
}
