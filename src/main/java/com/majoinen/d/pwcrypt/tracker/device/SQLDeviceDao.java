package com.majoinen.d.pwcrypt.tracker.device;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.encryption.utils.Tools;
import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class SQLDeviceDao implements DeviceDao {

    // Query to check if a device already exists in the database
    private static final String DEVICE_EXISTS_QUERY =
      "SELECT count(*) AS count FROM device WHERE " +
        "account_uuid = :account_uuid AND device_uuid = :device_uuid";

    // Query to insert new device information
    public static final String CREATE_DEVICE_QUERY =
      "INSERT INTO device (device_uuid, account_uuid, ip_address, " +
        "platform, public_key) VALUES (:device_uuid, :account_uuid, " +
        ":ip_address, :platform, :public_key)";

    // Query to insert device verification code
    public static final String INSERT_DEVICE_VERIFY_CODE =
      "INSERT INTO device_verify_code (device_uuid, verify_code) " +
        "VALUES (:device_uuid, :verify_code)";

    static final int VERIFY_CODE_LENGTH = 20;

    private static final int NEW_DEVICE_EXPECTED_AFFECTED_ROWS = 2;

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
        try {
            return 0 < databaseController
              .prepareQuery(DEVICE_EXISTS_QUERY)
              .setParameter(":account_uuid", accountUUID)
              .setParameter(":device_uuid", deviceUUID)
              .executeAndMap(resultSet -> resultSet.getInt("count"));
        } catch(DBUtilsException e) {
            throw new PwCryptException("Error checking if device exists", e);
        }
    }

    /**
     * Connect a device to an account. An email will be sent to the owner of
     * the account in order for them to verify the new device.
     *
     * @param accountUUID The UUID of the user.
     * @param device      The device info.
     * @return Activation code for the new device.
     */
    @Override
    public String addDevice(String accountUUID, Device device) {
        /* Generate verification code */
        String verifyCode = Tools.generateRandomString(VERIFY_CODE_LENGTH,
          Tools.ALPHA_NUMERIC);
        /* Add device to the database */
        try {
            if(NEW_DEVICE_EXPECTED_AFFECTED_ROWS == databaseController
              .prepareBatchQuery(SQLDeviceDao.CREATE_DEVICE_QUERY)
              .setParameter(":device_uuid", device.getUuid())
              .setParameter(":account_uuid", accountUUID)
              .setParameter(":ip_address", device.getIp())
              .setParameter(":platform", device.getPlatform())
              .setParameter(":public_key", device.getPublicKey())
              .prepareBatchQuery(SQLDeviceDao.INSERT_DEVICE_VERIFY_CODE)
              .setParameter(":device_uuid", device.getUuid())
              .setParameter(":verify_code", verifyCode)
              .executeUpdate()) {
                return verifyCode;
            } else {
                throw new PwCryptException("Unexpected affected row count");
            }
        } catch(DBUtilsException e) {
            throw new PwCryptException("Error adding new device", e);
        }
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
