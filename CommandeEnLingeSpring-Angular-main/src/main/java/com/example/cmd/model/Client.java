package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name="client")
public class Client extends Utilisateur {

    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;

    @Enumerated(EnumType.STRING)
    private StatusCompte status;


    public Client() {
        super();
    }

    public Client(String nom, String prenom, String username, String email, String motDePasse, String adresse, String telephone, RoleType roleType) {
        super(username, email, motDePasse, roleType);
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.status = StatusCompte.ACTIVE;
    }


}


