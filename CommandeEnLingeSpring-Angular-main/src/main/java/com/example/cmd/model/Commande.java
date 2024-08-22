package com.example.cmd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date = new Date();
    @ManyToOne
    private StatuCommande statu;
    @ManyToMany
    @JoinTable(
            name = "commande_produit",
            joinColumns = @JoinColumn(
                    name = "commande_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "produitcommandee_id"
            )
    )
    private List<ProduitCommandee> ProduitCommandees = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonIgnore
    private Livraison livraison;

    // Getters et setters
    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public float getTotalAvecLivraison() {
        float totalCommande = this.getProduitCommandees().stream()
                .map(pc -> pc.getProduit().getPrix() * pc.getQuantite())
                .reduce(0f, Float::sum);

        if (livraison != null) {
            return (float) (totalCommande + livraison.getTypeLivraison().getPrix());
        } else {
            return totalCommande;
        }
    }




}
