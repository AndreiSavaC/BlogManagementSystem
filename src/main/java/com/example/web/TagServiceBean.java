package com.example.web;

import com.example.entity.Tag;
import com.example.service.TagServiceLocal;
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

@Named("tagServiceBean")
@SessionScoped
public class TagServiceBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TagServiceBean.class.getName());

    @EJB
    private TagServiceLocal tagService;

    private List<Tag> tags;
    private String newTagName;

    private Long editingTagId;
    private String editingTagName;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing TagServiceBean");
        loadTags();
    }

    public void loadTags() {
        try {
            tags = tagService.findAll();
            LOGGER.info("Loaded " + tags.size() + " tags.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading tags", e);
        }
    }

    public String addTag() {
        if (newTagName == null || newTagName.trim().isEmpty()) {
            LOGGER.warning("Tag addition failed: Name is empty.");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tag name cannot be empty!", null));
            return null;
        }

        try {
            Tag tag = new Tag(newTagName.trim());
            tagService.create(tag);
            LOGGER.info("Tag added successfully: " + newTagName);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Tag added successfully!"));

            newTagName = "";
            loadTags();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding tag: " + newTagName, e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error adding tag!", null));
        }
        return null;
    }

    public void deleteTag(Long id) {
        try {
            tagService.delete(id);
            LOGGER.info("Tag deleted successfully with ID: " + id);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Tag deleted successfully!"));
            loadTags();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting tag with ID: " + id, e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting tag!", null));
        }
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getNewTagName() {
        return newTagName;
    }

    public void setNewTagName(String newTagName) {
        LOGGER.fine("Setting new tag name: " + newTagName);
        this.newTagName = newTagName;
    }
}
