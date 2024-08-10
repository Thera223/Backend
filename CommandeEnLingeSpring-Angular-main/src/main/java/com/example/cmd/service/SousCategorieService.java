package com.example.cmd.service;

import com.example.cmd.model.Category;
import com.example.cmd.model.SousCategory;
import com.example.cmd.repository.CategoryRepository;
import com.example.cmd.repository.SousCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class SousCategorieService {

    @Autowired
    SousCategoryRepository sousCategoryRepository;
    private CategoryService categoryService;

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
}


