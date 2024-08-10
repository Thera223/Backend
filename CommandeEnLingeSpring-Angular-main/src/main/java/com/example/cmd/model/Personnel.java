package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="personnel")
public class Personnel extends Utilisateur {

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public Personnel(String username, String email, String motDePasse, RoleType roleType, Admin admin) {
        super(username, email, motDePasse, roleType);
        this.admin = admin;
    }

}
