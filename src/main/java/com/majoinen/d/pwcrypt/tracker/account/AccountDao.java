package com.majoinen.d.pwcrypt.tracker.account;

import com.majoinen.d.pwcrypt.tracker.device.Device;
import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;

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
     * @throws PwCryptException If any exception occurs accessing the database.
     */
    boolean accountExists(String email) throws PwCryptException;

    /**
     * Create a new account in the database, adding the devices information
     * at the same time as a batch query.
     *
     * @param email Email of the new user
     * @param device Device information
     * @return A String array containing the account verification code and
     * device verification code to verify the newly created account.
     * @throws PwCryptException If there is an unexpected affected row count
     * when inserting into the database, or database error creating the account.
     */
    String[] createAccount(String email, Device device) throws PwCryptException;
}
