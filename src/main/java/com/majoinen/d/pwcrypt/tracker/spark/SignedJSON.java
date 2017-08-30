package com.majoinen.d.pwcrypt.tracker.spark;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public class SignedJSON {

    private String original;
    private String signature;

    public SignedJSON(String original, String signature) {
        this.original = original;
        this.signature = signature;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
