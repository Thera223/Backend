package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "UTILISATEUR")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Utilisateur implements Utilisateurs {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retourner les autorisations basées sur le rôle de l'utilisateur
        return List.of(() -> "ROLE_" + roleType.getNom());
    }

    @Override
    public String getPassword() {
        return this.motDePasse;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // À adapter selon vos besoins
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // À adapter selon vos besoins
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // À adapter selon vos besoins
    }

    @Override
    public boolean isEnabled() {
        return true; // À adapter selon vos besoins
    }

    @Override
    public void setAdmin(Admin admin) {
    }

    @Override
    public String getEmail() {
        return email;
    }
}