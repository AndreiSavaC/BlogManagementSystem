package com.example.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/emailQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
        }
)
public class EmailNotificationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationMDB.class.getName());

    @Resource(lookup = "mail/MyMailSession")
    private Session mailSession;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String emailJson = ((TextMessage) message).getText();
                LOGGER.info("Received JMS message for welcome email: " + emailJson);

                ObjectMapper mapper = new ObjectMapper();
                EmailPayload emailPayload = mapper.readValue(emailJson, EmailPayload.class);

                sendEmail(emailPayload);
            }
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Error reading JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing welcome email", ex);
        }
    }

    private void sendEmail(EmailPayload emailPayload) throws Exception {
        MimeMessage msg = new MimeMessage(mailSession);

        msg.setFrom(new InternetAddress("savacristianandrei@yahoo.com"));

        msg.setRecipient(RecipientType.TO, new InternetAddress(emailPayload.getTo()));

        msg.setSubject(emailPayload.getSubject());
        msg.setText(emailPayload.getBody());

        Transport.send(msg);

        LOGGER.info("Email sent to " + emailPayload.getTo());
    }
}

