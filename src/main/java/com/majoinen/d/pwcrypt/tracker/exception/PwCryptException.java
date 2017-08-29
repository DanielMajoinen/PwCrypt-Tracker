package com.majoinen.d.pwcrypt.tracker.exception;

/**
 * @author Daniel Majoinen
 * @version 1.0, 28/8/17
 */
public class PwCryptException extends RuntimeException {
    public PwCryptException() { }

    public PwCryptException(String message) {
        super(message);
    }

    public PwCryptException(Exception e) {
        super(e);
    }

    public PwCryptException(String message, Exception e) {
        super(message, e);
    }
}
