package com.example.service;

import com.example.entity.Category;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class CategoryService implements CategoryServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());

    @PersistenceContext(unitName = "BlogPU")
    private EntityManager em;

    @Override
    public void create(Category category) {
        try {
            em.persist(category);
            LOGGER.info("Category created successfully: " + category.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating category: " + category.getName(), e);
        }
    }

    @Override
    public Category findById(Long id) {
        try {
            Category category = em.find(Category.class, id);
            if (category != null) {
                LOGGER.info("Category found with ID: " + id);
            } else {
                LOGGER.warning("No category found with ID: " + id);
            }
            return category;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding category with ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Category> findAll() {
        try {
            List<Category> categories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
            LOGGER.info("Retrieved " + categories.size() + " categories");
            return categories;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all categories", e);
            return List.of();
        }
    }

    @Override
    public void update(Category category) {
        try {
            em.merge(category);
            LOGGER.info("Category updated successfully: " + category.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating category: " + category.getName(), e);
        }
    }

    @Override
    public boolean existsByName(String name) {
        try {
            Long count = em.createQuery("SELECT COUNT(c) FROM Category c WHERE c.name = :name", Long.class)
                    .setParameter("name", name)
                    .getSingleResult();
            boolean exists = count > 0;
            LOGGER.info("Category existence check for name '" + name + "': " + exists);
            return exists;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if category exists: " + name, e);
            return false;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
                LOGGER.info("Category deleted with ID: " + id);
            } else {
                LOGGER.warning("Attempted to delete non-existing category with ID: " + id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting category with ID: " + id, e);
        }
    }
}
