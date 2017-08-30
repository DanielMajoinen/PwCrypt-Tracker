package com.majoinen.d.pwcrypt.tracker.account;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public class RegisterRequest {

    private String email;
    private String deviceUUID;
    private String publicKey;

    public RegisterRequest(String email, String deviceUUID, String publicKey) {
        this.email = email;
        this.deviceUUID = deviceUUID;
        this.publicKey = publicKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
