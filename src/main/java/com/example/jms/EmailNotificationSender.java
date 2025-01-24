package com.example.jms;

import com.example.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class EmailNotificationSender {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationSender.class.getName());

    @Inject
    @JMSConnectionFactory("jms/__defaultConnectionFactory")
    private JMSContext jmsContext;

    @Resource(lookup = "jms/emailQueue")
    private Queue emailQueue;

    public void sendActivationEmail(User user) {
        try {
            EmailPayload emailPayload = new EmailPayload();
            emailPayload.setTo(user.getEmail());
            emailPayload.setSubject("Welcome to MyApp, " + user.getUsername() + "!");
            emailPayload.setBody("Hello " + user.getUsername() + ",\n\n"
                    + "Thank you for joining MyApp. Please activate your account by clicking the link below:\n"
                    + "http://localhost:8080/activate.xhtml?token=" + user.getActivationToken() + "\n\n"
                    + "Best regards,\nMyApp Team");

            ObjectMapper mapper = new ObjectMapper();
            String emailJson = mapper.writeValueAsString(emailPayload);

            jmsContext.createProducer().send(emailQueue, emailJson);
            LOGGER.info("Activation email sent successfully to " + user.getEmail());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send activation email to " + user.getEmail(), e);
        }
    }

    public void sendAccountActivatedEmail(User user) {
        try {
            EmailPayload emailPayload = new EmailPayload();
            emailPayload.setTo(user.getEmail());
            emailPayload.setSubject("Your MyApp Account is Activated!");
            emailPayload.setBody("Hello " + user.getUsername() + ",\n\n"
                    + "Your account has been successfully activated. You can now log in to MyApp.\n\n"
                    + "Best regards,\nMyApp Team");

            ObjectMapper mapper = new ObjectMapper();
            String emailJson = mapper.writeValueAsString(emailPayload);

            jmsContext.createProducer().send(emailQueue, emailJson);
            LOGGER.info("Account activation confirmation email sent successfully to " + user.getEmail());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send account activation confirmation email to " + user.getEmail(), e);
        }
    }
}
