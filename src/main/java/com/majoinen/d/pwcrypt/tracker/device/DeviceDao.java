package com.majoinen.d.pwcrypt.tracker.device;

import java.util.List;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public interface DeviceDao {

    /**
     * Determine if a device is already connected to an account.
     *
     * @param accountUUID The UUID of the user.
     * @param deviceUUID The UUID of the device.
     * @return True if the device already exists, or false otherwise.
     */
    boolean deviceExists(String accountUUID, String deviceUUID);

    /**
     * Connect a device to an account. An email will be sent to the owner of
     * the account in order for them to verify the new device.
     *
     * @param accountUUID The UUID of the user.
     * @param device The device info.
     * @return Activation code for the new device.
     */
    String addDevice(String accountUUID, Device device);

    /**
     * Authorises a device to an account. When the user signs in on a new
     * device, they are sent an email containing a verification code. This
     * code must be used inserted before they are allowed to login.
     *
     * @param code The verification code found in the verification email.
     * @return True if the device is verified successfully, or false otherwise.
     */
    boolean verifyDevice(String code);

    /**
     * Determines if a device is already verified. This can be used to resend
     * a verification email.
     *
     * @param accountUUID The accounts UUID.
     * @param deviceUUID The devices UUID.
     * @return True if it is already verified, or false otherwise.
     */
    boolean isVerified(String accountUUID, String deviceUUID);

    /**
     * Get the verification code for a device.
     *
     * @param accountUUID The accounts UUID.
     * @param deviceUUID The devices UUID.
     * @return The activation code.
     */
    String getVerifyCode(String accountUUID, String deviceUUID);

    /**
     * Supplies a list of all other devices associated with an account,
     * allowing the devices to begin communication with each other.
     *
     * @param accountUUID The UUID of the user.
     * @param deviceUUID The UUID of the current device.
     * @return A list of all other devices.
     */
    List<Device> listAllDevices(String accountUUID, String deviceUUID);

    /**
     * Gets the PublicKey associated with the supplied account and device
     * UUID's.
     *
     * @param accountUUID The UUID of the users account.
     * @param deviceUUID The UUID of the users device.
     * @return The PublicKey serialized and encoded in Base64.
     */
    String getPublicKey(String accountUUID, String deviceUUID);
}
