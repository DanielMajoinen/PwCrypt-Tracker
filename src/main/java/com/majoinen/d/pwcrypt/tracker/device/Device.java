package com.majoinen.d.pwcrypt.tracker.device;

/**
 * @author Daniel Majoinen
 * @version 1.0, 12/7/17
 */
public class Device {

    private String uuid;
    private String ip;
    private String platform;
    private String publicKey;

    public Device(String uuid, String ip, String platform, String publicKey) {
        this.uuid = uuid;
        this.ip = ip;
        this.platform = platform;
        this.publicKey = publicKey;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof Device))
            return false;
        Device device = (Device) obj;
        return this.uuid.equals(device.getUuid()) &&
          this.ip.equals(device.getIp()) &&
          this.platform.equals(device.getPlatform()) &&
          this.publicKey.equals(device.getPublicKey());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + uuid.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + platform.hashCode();
        result = 31 * result + publicKey.hashCode();
        return result;
    }
}
