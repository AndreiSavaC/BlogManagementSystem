package com.example.service;

import com.example.entity.BlogPost;
import com.example.entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class BlogPostService implements BlogPostServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(BlogPostService.class.getName());

    @PersistenceContext(unitName = "BlogPU")
    private EntityManager em;

    @EJB
    private UserServiceLocal userService;

    @Override
    public void create(BlogPost post) {
        try {
            User user = userService.findByUsername(post.getUser().getUsername());
            if (user != null) {
                post.setUser(user);
                em.persist(post);
                LOGGER.info("Blog post created successfully for user: " + user.getUsername());
            } else {
                throw new IllegalArgumentException("User not found");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating blog post", e);
        }
    }

    @Override
    public BlogPost findById(Long id) {
        try {
            BlogPost post = em.find(BlogPost.class, id);
            if (post != null) {
                LOGGER.info("Blog post found with ID: " + id);
            } else {
                LOGGER.warning("No blog post found with ID: " + id);
            }
            return post;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding blog post with ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<BlogPost> findAll() {
        try {
            List<BlogPost> posts = em.createQuery("SELECT p FROM BlogPost p", BlogPost.class).getResultList();
            LOGGER.info("Retrieved " + posts.size() + " blog posts");
            return posts;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all blog posts", e);
            return List.of();
        }
    }

    @Override
    public void update(BlogPost post) {
        try {
            em.merge(post);
            LOGGER.info("Blog post updated successfully with ID: " + post.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating blog post with ID: " + post.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            BlogPost post = em.find(BlogPost.class, id);
            if (post != null) {
                em.remove(post);
                LOGGER.info("Blog post deleted with ID: " + id);
            } else {
                LOGGER.warning("Attempted to delete non-existing blog post with ID: " + id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting blog post with ID: " + id, e);
        }
    }
}
