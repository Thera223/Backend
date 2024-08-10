package com.example.cmd.repository;

import com.example.cmd.model.Category;
import com.example.cmd.model.SousCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SousCategoryRepository extends JpaRepository<SousCategory, Long> {
    SousCategory findByLibelle(String libelle);


    List<SousCategory> findByCategory(Category category);
}
