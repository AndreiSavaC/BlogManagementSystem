package com.example.web;

import com.example.entity.Category;
import com.example.service.CategoryServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("categoryServiceBean")
@SessionScoped
public class CategoryServiceBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CategoryServiceBean.class.getName());

    @EJB
    private CategoryServiceLocal categoryService;

    private List<Category> categories;
    private String newCategoryName;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing CategoryServiceBean");
        loadCategories();
    }

    public void loadCategories() {
        try {
            categories = categoryService.findAll();
            LOGGER.info("Loaded " + categories.size() + " categories.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading categories", e);
        }
    }

    public String addCategory() {
        if (newCategoryName == null || newCategoryName.trim().isEmpty()) {
            LOGGER.warning("Category addition failed: Name is empty.");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category name cannot be empty!", null));
            return null;
        }

        try {
            if (categoryService.existsByName(newCategoryName.trim())) {
                LOGGER.warning("Category addition failed: Category already exists with name: " + newCategoryName);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category already exists!", null));
                return null;
            }

            Category cat = new Category(newCategoryName.trim());
            categoryService.create(cat);
            LOGGER.info("Category added successfully: " + newCategoryName);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Category added successfully!"));

            newCategoryName = "";
            loadCategories();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding category: " + newCategoryName, e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error adding category!", null));
        }
        return null;
    }

    public void deleteCategory(Long id) {
        try {
            categoryService.delete(id);
            LOGGER.info("Category deleted successfully with ID: " + id);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Category deleted successfully!"));
            loadCategories();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting category with ID: " + id, e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting category!", null));
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getNewCategoryName() {
        return newCategoryName;
    }

    public void setNewCategoryName(String newCategoryName) {
        LOGGER.fine("Setting new category name: " + newCategoryName);
        this.newCategoryName = newCategoryName;
    }
}
