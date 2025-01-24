package com.example.web;

import com.example.entity.User;
import com.example.service.UserServiceLocal;
import com.example.jms.EmailNotificationSender;
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
public class ActivationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ActivationBean.class.getName());

    @EJB
    private UserServiceLocal userService;

    @EJB
    private EmailNotificationSender emailNotificationSender;

    private String token;

    public String activate() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (token == null || token.isEmpty()) {
            LOGGER.warning("Activation attempted with empty token.");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid activation token.", null));
            return null;
        }

        User user = userService.findByActivationToken(token);

        if (user == null) {
            LOGGER.warning("Activation attempted with invalid token: " + token);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid activation token.", null));
            return null;
        }

        if (user.isActive()) {
            LOGGER.info("Account already activated for user: " + user.getUsername());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Account already activated.", null));
            return "login.xhtml?faces-redirect=true";
        }

        user.setActive(true);
        user.setActivationToken(null);
        userService.update(user);
        LOGGER.info("User activated successfully: " + user.getUsername());

        try {
            emailNotificationSender.sendAccountActivatedEmail(user);
            LOGGER.info("Activation confirmation email sent to: " + user.getEmail());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending activation confirmation email to " + user.getEmail(), e);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error sending confirmation email.", null));
            return null;
        }

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Account successfully activated. You can now log in.", null));
        return "login.xhtml?faces-redirect=true";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
