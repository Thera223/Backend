package com.example.cmd.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@Table(name="ADMIN")
@Entity

public class Admin extends Utilisateur {

    public Admin() {}

    public Admin(String username, String email, String motDePasse, RoleType roletype){
        super(username, email, motDePasse, roletype);


    }


}
