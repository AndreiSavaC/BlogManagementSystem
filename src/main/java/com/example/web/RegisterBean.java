package com.example.web;

import com.example.entity.User;
import com.example.jms.EmailNotificationSender;
import com.example.service.UserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
public class RegisterBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RegisterBean.class.getName());

    @EJB
    private UserServiceLocal userService;

    @EJB
    private EmailNotificationSender emailNotificationSender;

    private String username;
    private String email;
    private String password;
    private String role = "USER";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        LOGGER.fine("Setting username: " + username);
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        LOGGER.fine("Setting email: " + email);
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        LOGGER.fine("Setting password.");
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        LOGGER.fine("Setting role: " + role);
        this.role = role;
    }

    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();

        LOGGER.info("Attempting to register user: " + username);

        if (userService.findByUsername(username) != null) {
            LOGGER.warning("Registration failed: Username already exists.");
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username already exists.", null));
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        try {
            userService.create(user);
            LOGGER.info("User registered successfully: " + username);

            emailNotificationSender.sendActivationEmail(user);
            LOGGER.info("Activation email sent to: " + email);

            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration successful! Please check your email to activate your account.", null));
            return "login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during registration for user: " + username, e);
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error during registration.", null));
            return null;
        }
    }
}
