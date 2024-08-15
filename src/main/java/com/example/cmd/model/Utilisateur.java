package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "UTILISATEUR")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String motDePasse;


    @Getter
    @ManyToOne
    @JoinColumn(name = "id_role")
    private RoleType roleType;

    public Utilisateur(String username, String email, String motDePasse, RoleType roleType) {
        this.username = username;
        this.email= email;
        this.motDePasse = motDePasse;
        this.roleType = roleType;
    }


    public void setAdmin(Admin admin) {
    }

    public String getEmail() {
        return email;
    }
}