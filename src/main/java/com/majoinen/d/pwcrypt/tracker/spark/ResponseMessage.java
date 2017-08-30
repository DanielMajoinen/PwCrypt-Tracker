package com.majoinen.d.pwcrypt.tracker.spark;

/**
 * @author Daniel Majoinen
 * @version 1.0, 29/8/17
 */
public class ResponseMessage {

    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public ResponseMessage(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}
