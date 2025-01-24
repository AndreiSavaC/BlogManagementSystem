package com.example.service;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
@Singleton
public class BlogPostChecker {

    private static final Logger LOGGER = Logger.getLogger(BlogPostChecker.class.getName());

    @PersistenceContext(unitName = "BlogPU")
    private EntityManager em;

    private int newPostsCount = 0;

    @Schedule(hour = "*", minute = "0", second = "0", persistent = false)
    public void checkNewPosts() {
        try {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            List<Long> newPosts = em.createQuery(
                            "SELECT COUNT(p) FROM BlogPost p WHERE p.createdAt >= :time", Long.class
                    ).setParameter("time", oneHourAgo)
                    .getResultList();

            newPostsCount = newPosts.isEmpty() ? 0 : newPosts.get(0).intValue();

            LOGGER.info("New posts in last hour: " + newPostsCount);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking new blog posts", e);
        }
    }

    public int getNewPostsCount() {
        return newPostsCount;
    }
}
