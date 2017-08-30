package com.majoinen.d.pwcrypt.tracker.mail;

import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @author Daniel Majoinen
 * @version 1.0, 29/8/17
 */
public class EmailController {

    private static final String REGISTER_SUBJECT = "PwCrypt Email Verification";

    private static final String DEVICE_UUID_PROMPT = "Device UUID: \n";
    private static final String REGISTER_TEXT = "\nVerification Code: \n";

    /**
     * Send a registration email to a newly registered user. This will send
     * them their verification code.
     *
     * @param recipient The newly registered users email address.
     * @param deviceUUID The users device UUID.
     * @param code The users verification code.
     */
    public static void sendRegisterEmail(String recipient, String deviceUUID,
      String code) {
        try {
            Message message = new MimeMessage(Gmail.createSession());
            message.setSentDate(new Date());
            message.setFrom(new InternetAddress(
              Gmail.credentials().getProperty("username")));
            message.setRecipient(Message.RecipientType.TO,
              new InternetAddress(recipient));
            message.setSubject(REGISTER_SUBJECT);
            message.setText(DEVICE_UUID_PROMPT + deviceUUID +
              REGISTER_TEXT + code);
            sendMessage(message);
        } catch(MessagingException e) {
            throw new PwCryptException("Error creating message", e);
        }
    }

    /**
     * Send a prepared message to the recipient, providing error handling.
     *
     * @param message The message to send.
     */
    private static void sendMessage(Message message) {
        try {
            Transport.send(message);
        } catch(MessagingException e) {
            throw new PwCryptException("Error sending message", e);
        }
    }
}
