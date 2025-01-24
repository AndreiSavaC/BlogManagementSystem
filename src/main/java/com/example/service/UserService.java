package com.example.service;

import com.example.entity.User;
import com.example.util.HashUtil;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class UserService implements UserServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    @PersistenceContext(unitName = "BlogPU")
    private EntityManager em;

    @Override
    public void create(User user) {
        try {
            String hashedPassword = HashUtil.sha256Hex(user.getPassword());
            user.setPassword(hashedPassword);
            user.setActive(false);
            user.setActivationToken(UUID.randomUUID().toString());
            em.persist(user);
            LOGGER.info("User created successfully: " + user.getUsername());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating user: " + user.getUsername(), e);
        }
    }

    @Override
    public User findByActivationToken(String token) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.activationToken = :token", User.class)
                    .setParameter("token", token)
                    .getSingleResult();
            LOGGER.info("User found for activation token.");
            return user;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No user found for activation token: " + token, e);
            return null;
        }
    }

    @Override
    public User findById(Long id) {
        try {
            User user = em.find(User.class, id);
            if (user != null) {
                LOGGER.info("User found with ID: " + id);
            } else {
                LOGGER.warning("No user found with ID: " + id);
            }
            return user;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding user with ID: " + id, e);
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            LOGGER.info("User found with username: " + username);
            return user;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No user found with username: " + username, e);
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
            LOGGER.info("Retrieved " + users.size() + " users.");
            return users;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
            return List.of();
        }
    }

    @Override
    public void update(User user) {
        try {
            em.merge(user);
            LOGGER.info("User updated successfully: " + user.getUsername());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getUsername(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            User user = em.find(User.class, id);
            if (user != null) {
                em.createQuery("DELETE FROM BlogPost b WHERE b.user.id = :userId")
                        .setParameter("userId", id)
                        .executeUpdate();
                em.remove(user);
                LOGGER.info("User deleted with ID: " + id);
            } else {
                LOGGER.warning("Attempted to delete non-existing user with ID: " + id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
        }
    }
}
