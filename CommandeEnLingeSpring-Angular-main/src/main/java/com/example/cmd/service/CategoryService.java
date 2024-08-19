package com.example.cmd.service;

import com.example.cmd.DTO.CategoryDto;
import com.example.cmd.model.Category;
import com.example.cmd.model.FileInfo;
import com.example.cmd.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> CategoryList = new ArrayList<>();
        List<Category> categiries = categoryRepository.findAll();
        categiries.forEach(category -> {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setLibelle(category.getLibelle());
            FileInfo fileInfo = category.getFileInfo();
            String imagePath = String.format("http://localhost:8080/admin/files/"+fileInfo.getName());
            categoryDto.setImagePath(imagePath);
            CategoryList.add(categoryDto);
        });
        return CategoryList;
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category getCategoryBy(String libelle) {
        return categoryRepository.findByLibelle(libelle);
    }

    public Category createCategory(Category category) {
//        Category category = new Category();
//        category.setLibelle(name);
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
