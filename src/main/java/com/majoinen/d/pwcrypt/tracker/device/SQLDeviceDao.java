package com.majoinen.d.pwcrypt.tracker.device;

import com.majoinen.d.database.DatabaseController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLDeviceDao implements DeviceDao {

    private static Map<DatabaseController, SQLDeviceDao> map;
    private DatabaseController databaseController;

    private SQLDeviceDao(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    /**
     * Retrieval method for appropriate SQLDeviceDao associated with supplied
     * DatabaseController. It will create an instance if it does not
     * exist and caches it.
     *
     * @param databaseController DatabaseController which maps to
     * desired SQLDeviceDao.
     * @return SQLDeviceDao which interfaces with supplied DatabaseController.
     */
    public static SQLDeviceDao getInstance(DatabaseController databaseController) {
        if(map == null)
            map = new HashMap<>();
        else if(map.containsKey(databaseController))
            return map.get(databaseController);
        SQLDeviceDao deviceDao = new SQLDeviceDao(databaseController);
        map.put(databaseController, deviceDao);
        return deviceDao;
    }

    /**
     * Determine if a device is already connected to an account.
     *
     * @param accountUUID The UUID of the user.
     * @param deviceUUID  The UUID of the device.
     * @return True if the device already exists, or false otherwise.
     */
    @Override
    public boolean deviceExists(String accountUUID, String deviceUUID) {
        return false;
    }

    /**
     * Connect a device to an account. An email will be sent to the owner of
     * the account in order for them to verify the new device.
     *
     * @param accountUUID The UUID of the user.
     * @param device      The device info.
     * @return True if the device is added successfully, or false otherwise.
     */
    @Override
    public boolean addDevice(String accountUUID, Device device) {
        return false;
    }

    /**
     * Authorises a device to an account. When the user signs in on a new
     * device, they are sent an email containing a verification code. This
     * code must be used inserted before they are allowed to login.
     *
     * @param code The verification code found in the verification email.
     * @return True if the device is verified successfully, or false otherwise.
     */
    @Override
    public boolean verifyDevice(String code) {
        return false;
    }

    /**
     * Supplies a list of all other devices associated with an account,
     * allowing the devices to begin communication with each other.
     *
     * @param accountUUID The UUID of the user.
     * @param deviceUUID  The UUID of the current device.
     * @return A list of all other devices.
     */
    @Override
    public List<Device> listAllDevices(String accountUUID, String deviceUUID) {
        return null;
    }
}
