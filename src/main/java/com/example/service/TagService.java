package com.example.service;

import com.example.entity.Tag;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class TagService implements TagServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(TagService.class.getName());

    @PersistenceContext(unitName = "BlogPU")
    private EntityManager em;

    @Override
    public void create(Tag tag) {
        try {
            em.persist(tag);
            LOGGER.info("Tag created successfully: " + tag.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating tag: " + tag.getName(), e);
        }
    }

    @Override
    public Tag findById(Long id) {
        try {
            Tag tag = em.find(Tag.class, id);
            if (tag != null) {
                LOGGER.info("Tag found with ID: " + id);
            } else {
                LOGGER.warning("No tag found with ID: " + id);
            }
            return tag;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding tag with ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Tag> findAll() {
        try {
            List<Tag> tags = em.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
            LOGGER.info("Retrieved " + tags.size() + " tags");
            return tags;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all tags", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Set<Tag> findByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            LOGGER.info("Empty tag ID set provided, returning empty result.");
            return Collections.emptySet();
        }
        try {
            List<Tag> tagList = em.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class)
                    .setParameter("ids", ids)
                    .getResultList();
            LOGGER.info("Retrieved " + tagList.size() + " tags for given IDs.");
            return new HashSet<>(tagList);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving tags by IDs", e);
            return Collections.emptySet();
        }
    }

    @Override
    public void update(Tag tag) {
        try {
            em.merge(tag);
            LOGGER.info("Tag updated successfully: " + tag.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating tag: " + tag.getName(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Tag tag = em.find(Tag.class, id);
            if (tag != null) {
                em.remove(tag);
                LOGGER.info("Tag deleted with ID: " + id);
            } else {
                LOGGER.warning("Attempted to delete non-existing tag with ID: " + id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting tag with ID: " + id, e);
        }
    }
}
