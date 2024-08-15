package com.example.cmd.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Map;

@Data
@Entity
@Table(name = "ProductVariant")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "VariantAttributes", joinColumns = @JoinColumn(name = "variant_id"))
    @MapKeyColumn(name = "attribute_id")
    @Column(name = "value")
    private Map<Long, String> attributes; // Utilise Long pour les clés (ID des attributs)

    @ManyToOne
    @JoinColumn(name = "sous_category_id")
    @JsonBackReference
    private SousCategory sousCategory;


}