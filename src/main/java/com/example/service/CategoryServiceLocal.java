package com.example.service;

import com.example.entity.Category;
import java.util.List;
import jakarta.ejb.Local;

@Local
public interface CategoryServiceLocal {
    void create(Category category);
    Category findById(Long id);
    List<Category> findAll();
    void update(Category category);
    void delete(Long id);
    boolean existsByName(String name);
}
