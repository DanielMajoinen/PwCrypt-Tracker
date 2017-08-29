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
     * @return A String array containing the account verification code and
     * device verification code to verify the newly created account.
     */
    String[] createAccount(String email, Device device);
}
