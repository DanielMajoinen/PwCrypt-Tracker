package com.majoinen.d.pwcrypt.tracker.account;

import com.majoinen.d.pwcrypt.tracker.device.Device;

/**
 * @author Daniel Majoinen
 * @version 1.0, 28/8/17
 */
public interface AccountDao {

    /**
     * Determine if an account exists with the supplied email address.
     *
     * @param email The email address to check for.
     * @return True if the email is already in use, or false otherwise.
     */
    boolean accountExists(String email);

    /**
     * Create a new account in the database, adding the devices information
     * at the same time as a batch query.
     *
     * @param email Email of the new user
     * @param device Device information
     * @return A String containing the account verification code.
     */
    String createAccount(String email, Device device);

    /**
     * Gets the account UUID associated with an email.
     *
     * @param email The email of the user.
     * @return The account UUID of the user.
     */
    String getAccountUUID(String email);
}
