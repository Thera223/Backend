package com.example.cmd.service;

import com.example.cmd.model.Category;
import com.example.cmd.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category getCategoryBy(String libelle) {
        return categoryRepository.findByLibelle(libelle);
    }

    public Category createCategory(String name) {
        Category category = new Category();
        category.setLibelle(name);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, String name) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setLibelle(name);
            return categoryRepository.save(category);
        }
        return null;
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
