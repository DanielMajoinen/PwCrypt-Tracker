package com.majoinen.d.pwcrypt.tracker.device;

/**
 * @author Daniel Majoinen
 * @version 1.0, 31/8/17
 */
public class DeviceVerificationResponse {

    private String accountUUID;

    public DeviceVerificationResponse(String accountUUID) {
        this.accountUUID = accountUUID;
    }

    public String getAccountUUID() {
        return accountUUID;
    }

    public void setAccountUUID(String accountUUID) {
        this.accountUUID = accountUUID;
    }
}
