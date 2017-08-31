package com.majoinen.d.pwcrypt.tracker.device;

/**
 * @author Daniel Majoinen
 * @version 1.0, 31/8/17
 */
public class DeviceVerificationRequest {

    private String email;
    private String deviceUUID;
    private String code;

    public DeviceVerificationRequest(String email, String deviceUUID,
      String code) {
        this.email = email;
        this.deviceUUID = deviceUUID;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
