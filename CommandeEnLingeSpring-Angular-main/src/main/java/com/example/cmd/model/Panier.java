package com.example.cmd.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float total;
    @ManyToOne

    private Client client;

    @ManyToMany
    @JoinTable(
            name = "panier_produit",
            joinColumns = @JoinColumn(name = "panier_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private List<ProduitCommandee> produits = new ArrayList<>();



    public void ajouterProduit(ProduitCommandee produit, int quantite) {
        for (ProduitCommandee p : produits) {
            if (p.getId().equals(produit.getId())) {
                p.setQuantite(p.getQuantite() + quantite);
                return;
            }
        }
        produit.setQuantite(quantite);
        produits.add(produit);

        this.total = getTotal();

    }


    public void modifierQuantiteProduit(Long produitId, int nouvelleQuantite) {
        for (ProduitCommandee produit : produits) {
            if (produit.getId().equals(produitId)) {
                produit.setQuantite(nouvelleQuantite);
                this.total = getTotal();
                return;

            }
        }
        throw new RuntimeException("Produit non trouvé dans le panier");

    }

    public void supprimerProduit(Long produitId) {

        produits.removeIf(produit -> produit.getId().equals(produitId));
        this.total = getTotal();
    }


    public float getTotal() {
        return (float) produits.stream()
                .mapToDouble(p -> p.getProduit().getPrix() * p.getQuantite())
                .sum();
    }

}
