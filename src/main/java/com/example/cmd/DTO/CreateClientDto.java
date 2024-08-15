package com.example.cmd.DTO;

import lombok.Data;

@Data
public class CreateClientDto {
    private String nom;
    private String prenom;
    private String username;
    private String email;
    private String motDePasse;
    private String adresse;
    private String telephone;
}
