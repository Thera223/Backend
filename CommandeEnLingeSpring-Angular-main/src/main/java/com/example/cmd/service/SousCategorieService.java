package com.example.cmd.service;

import com.example.cmd.model.Category;
import com.example.cmd.model.ProductAttribute;
import com.example.cmd.model.ProductVariant;
import com.example.cmd.model.SousCategory;
import com.example.cmd.repository.CategoryRepository;
import com.example.cmd.repository.ProductVariantRepository;
import com.example.cmd.repository.SousCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class SousCategorieService {

    @Autowired
    SousCategoryRepository sousCategoryRepository;
    private CategoryService categoryService;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    CategoryRepository categoryRepository;


    public List<SousCategory> getAllSousCategory() {
        return this.sousCategoryRepository.findAll();
    }

    public SousCategory getCategory(Long id) {
        return this.sousCategoryRepository.findById(id).orElse(null);
    }

    public SousCategory getCategoryBy(String libelle) {
        return this.sousCategoryRepository.findByLibelle(libelle);
    }

    public SousCategory createSousCategory(SousCategory sousCategory) {
        SousCategory sousCat = new SousCategory();
        Category category = categoryService.getCategory(sousCategory.getCategory().getId());
        sousCat.setLibelle(sousCategory.getLibelle());
        sousCat.setCategory(category);
        return this.sousCategoryRepository.save(sousCat);
    }
    public SousCategory createSousCategoryN(String name, Long id) {
        SousCategory sousCategory = new SousCategory();
        sousCategory.setLibelle(name);
        Category category = categoryRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Pas de categoris" + id));
        sousCategory.setCategory(category);
        return sousCategoryRepository.save(sousCategory);
    }

    public SousCategory updateCategory(Long id, String libelle) {
        SousCategory sousCategory = this.sousCategoryRepository.findById(id).orElse(null);
        if (sousCategory != null) {
            sousCategory.setLibelle(libelle);
            return this.sousCategoryRepository.save(sousCategory);
        }
        return null;
    }

    public void deleteSousCategory(Long id) {
        this.sousCategoryRepository.deleteById(id);
    }
    public List<SousCategory> finBycategorie(Category category) {
        return this.sousCategoryRepository.findByCategory(category);


    }
    // Gestion des variantes de produits
    public List<ProductVariant> getVariantsBySousCategory(Long sousCategoryId) {
        SousCategory sousCategory = getCategory(sousCategoryId);
        if (sousCategory != null) {
            return productVariantRepository.findBySousCategory(sousCategory);
        }
        throw new NoSuchElementException("Sous-category not found for id: " + sousCategoryId);
    }

    public ProductVariant createVariantForSousCategory(Long sousCategoryId, Map<ProductAttribute, List<String>> attributes) {
        // Vérifier si la sous-catégorie existe
        SousCategory sousCategory = sousCategoryRepository.findById(sousCategoryId)
                .orElseThrow(() -> new NoSuchElementException("Sous-catégorie non trouvée pour ID : " + sousCategoryId));

        // Créer une nouvelle variante de produit
        ProductVariant variant = new ProductVariant();
        variant.setSousCategory(sousCategory); // Assigner la sous-catégorie

        // Convertir les attributs en Map<Long, String> pour correspondre à la structure de ProductVariant
        Map<Long, String> attributeMap = new HashMap<>();
        for (Map.Entry<ProductAttribute, List<String>> entry : attributes.entrySet()) {
            Long attributeId = entry.getKey().getId();
            List<String> values = entry.getValue();

            // Ici, nous ne prenons que le premier valeur pour l'exemple
            if (!values.isEmpty()) {
                attributeMap.put(attributeId, values.get(0)); // Utiliser la première valeur comme exemple
            }
        }

        // Assigner les attributs à la variante
        variant.setAttributes(attributeMap);

        // Enregistrez la variante dans la base de données
        return productVariantRepository.save(variant);
    }
}


