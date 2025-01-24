package com.example.web;

import com.example.entity.User;
import com.example.service.UserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Named
@SessionScoped
public class UserBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserBean.class.getName());

    @EJB
    private UserServiceLocal userService;

    @Inject
    private BlogPostBean blogPostBean;

    private String username;
    private String password;
    private List<User> allUsers;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing UserBean");
        loadUsers();
    }

    public void loadUsers() {
        try {
            allUsers = userService.findAll();
            LOGGER.info("Loaded " + allUsers.size() + " users.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading users", e);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        LOGGER.fine("Setting username: " + username);
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        LOGGER.fine("Setting password.");
        this.password = password;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        LOGGER.fine("Setting all users.");
        this.allUsers = allUsers;
    }

    public void redirectIfLoggedIn() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (getLoggedInUsername() != null) {
            LOGGER.info("User already logged in: " + getLoggedInUsername());
            try {
                if (isAdmin()) {
                    LOGGER.info("Redirecting to admin page.");
                    context.getExternalContext().redirect("admin/admin.xhtml");
                } else if (isUser()) {
                    LOGGER.info("Redirecting to user page.");
                    context.getExternalContext().redirect("user/user.xhtml");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error redirecting user", e);
            }
        }
    }

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
                (HttpServletRequest) context.getExternalContext().getRequest();

        LOGGER.info("Attempting to log in user: " + username);

        try {
            request.login(username, password);
            LOGGER.info("User logged in successfully: " + username);

            User user = userService.findByUsername(username);
            if (user == null || !user.isActive()) {
                request.logout();
                LOGGER.warning("Login failed: User not found or not active - " + username);
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account is not activated. Please check your email.", null));
                return null;
            }

            if (request.isUserInRole("ADMIN")) {
                LOGGER.info("User " + username + " has role ADMIN. Redirecting to admin page.");
                return "/admin/admin.xhtml?faces-redirect=true";
            } else if (request.isUserInRole("USER")) {
                LOGGER.info("User " + username + " has role USER. Redirecting to user page.");
                return "/user/user.xhtml?faces-redirect=true";
            } else {
                LOGGER.warning("User " + username + " has an invalid role.");
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid role.", null));
                return null;
            }
        } catch (ServletException e) {
            LOGGER.log(Level.WARNING, "Login failed for user: " + username, e);
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username or password.", null));
            return null;
        }
    }

    public String getLoggedInUsername() {
        FacesContext context = FacesContext.getCurrentInstance();
        String remoteUser = context.getExternalContext().getRemoteUser();
        LOGGER.fine("Logged in username: " + remoteUser);
        return remoteUser;
    }

    public boolean isAdmin() {
        HttpServletRequest request =
                (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        boolean admin = request.isUserInRole("ADMIN");
        LOGGER.fine("isAdmin: " + admin);
        return admin;
    }

    public boolean isUser() {
        HttpServletRequest request =
                (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        boolean user = request.isUserInRole("USER");
        LOGGER.fine("isUser: " + user);
        return user;
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
                (HttpServletRequest) context.getExternalContext().getRequest();

        LOGGER.info("Attempting to log out user: " + getLoggedInUsername());

        try {
            request.logout();
            LOGGER.info("User logged out successfully: " + getLoggedInUsername());

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
                LOGGER.fine("Session invalidated.");
            }

            return "/login.xhtml?faces-redirect=true";
        } catch (ServletException e) {
            LOGGER.log(Level.SEVERE, "Error during logout for user: " + getLoggedInUsername(), e);
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error during logout.", null));
            return null;
        }
    }

    public String deleteUser(Long id) {
        LOGGER.info("Attempting to delete user with ID: " + id);
        try {
            userService.delete(id);
            LOGGER.info("User deleted successfully with ID: " + id);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("User deleted successfully!"));

            loadUsers();
            blogPostBean.loadBlogPosts();

            return "manageUsers.xhtml?faces-redirect=true";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting user.", null));
            return null;
        }
    }
}
