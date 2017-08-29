package com.majoinen.d.pwcrypt.tracker.mail;

import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Daniel Majoinen
 * @version 1.0, 29/8/17
 */
public class Gmail {

    private static final String GMAIL_CONFIG = "/config/gmail.properties";

    private static final String GMAIL_CREDS_CONFIG =
      "/config/gmail-creds.properties";

    private static Properties properties = null;
    private static Properties credentials = null;

    /**
     * Create a session for sending emails with.
     *
     * @return An authenticated session.
     */
    static Session createSession() {
        Session session = Session.getDefaultInstance(Gmail.properties(),
          new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(
                    Gmail.credentials().getProperty("username"),
                    Gmail.credentials().getProperty("password"));
              }
          });
        return session;
    }

    /**
     * Get the GMail configuration properties.
     *
     * @return GMail configuration in form of Properties.
     */
    private static Properties properties() {
        if(properties == null)
            properties = getResource(GMAIL_CONFIG);
        return properties;
    }

    /**
     * Get the GMail account credentials.
     *
     * @return GMail credentials in form of Properties.
     */
    static Properties credentials() {
        if(credentials == null)
            credentials = getResource(GMAIL_CREDS_CONFIG);
        return credentials;
    }

    /**
     * Reads the supplied resource file as a resource and loads as a Properties.
     *
     * @param location Resource file location.
     * @return Properties of the supplied file.
     */
    private static Properties getResource(String location) {
        try {
            Properties properties = new Properties();
            properties.load(Gmail.class.getResourceAsStream(location));
            return properties;
        } catch(IOException e) {
            throw new PwCryptException("Error reading properties file", e);
        }
    }
}
