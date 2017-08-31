package com.majoinen.d.pwcrypt.tracker.device;

/**
 * @author Daniel Majoinen
 * @version 1.0, 31/8/17
 */
public class ListDeviceRequest {

    private String accountUUID;
    private String deviceUUID;

    public ListDeviceRequest(String accountUUID, String deviceUUID) {
        this.accountUUID = accountUUID;
        this.deviceUUID = deviceUUID;
    }

    public String getAccountUUID() {
        return accountUUID;
    }

    public void setAccountUUID(String accountUUID) {
        this.accountUUID = accountUUID;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }
}
