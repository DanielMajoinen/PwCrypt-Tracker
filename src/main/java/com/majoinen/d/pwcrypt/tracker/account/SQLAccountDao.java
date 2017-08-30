package com.majoinen.d.pwcrypt.tracker.account;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.encryption.utils.Tools;
import com.majoinen.d.pwcrypt.tracker.device.Device;
import com.majoinen.d.pwcrypt.tracker.device.SQLDeviceDao;
import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel Majoinen
 * @version 1.0, 28/8/17
 */
public class SQLAccountDao implements AccountDao {

    // Query to check if an account already exists in the database
    public static final String ACCOUNT_EXISTS_QUERY =
      "SELECT count(*) AS count FROM account WHERE email = :email";

    // Query to create a new account
    public static final String CREATE_ACCOUNT_QUERY =
      "INSERT INTO account (account_uuid, email) " +
        "VALUES (:account_uuid, :email)";

    private static final int NEW_ACCOUNT_EXPECTED_AFFECTED_ROWS = 3;

    static final int VERIFY_CODE_LENGTH = 5;

    private static Map<DatabaseController, SQLAccountDao> map;
    private DatabaseController databaseController;

    private SQLAccountDao(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    /**
     * Retrieval method for appropriate AccountDao associated with supplied
     * DatabaseController. It will create an instance if it does not
     * exist and caches it.
     *
     * @param databaseController DatabaseController which maps to
     * desired SQLAccountDao.
     * @return SQLAccountDao which interfaces with supplied DatabaseController.
     */
    public static SQLAccountDao getInstance(DatabaseController
      databaseController) {
        if(map == null)
            map = new HashMap<>();
        else if(map.containsKey(databaseController))
            return map.get(databaseController);
        SQLAccountDao accountDao = new SQLAccountDao(databaseController);
        map.put(databaseController, accountDao);
        return accountDao;
    }

    /**
     * Determine if an account exists with the supplied email address.
     *
     * @param email The email address to check for.
     * @return True if the email is already in use, or false otherwise.
     */
    @Override
    public boolean accountExists(String email) {
        try {
            return 0 < databaseController
              .prepareQuery(ACCOUNT_EXISTS_QUERY)
              .setParameter(":email", email)
              .executeAndMap(resultSet -> resultSet.getInt("count"));
        } catch(DBUtilsException e) {
            throw new PwCryptException("Error checking if account exists", e);
        }
    }

    /**
     * Create a new account in the database, adding the devices information
     * at the same time as a batch query.
     *
     * @param email Email of the new user
     * @param device Device information
     * @return A String containing the account verification code.
     */
    @Override
    public String createAccount(String email, Device device) {
        /* Generate an account UUID */
        String accountUUID = UUID.randomUUID().toString();
        /* Generate verification code */
        String verifyCode = Tools.generateRandomString(VERIFY_CODE_LENGTH,
          Tools.ALPHA_NUMERIC);
        /* Add account to database */
        try {
            if(NEW_ACCOUNT_EXPECTED_AFFECTED_ROWS == databaseController
              .prepareBatchQuery(CREATE_ACCOUNT_QUERY)
              .setParameter(":account_uuid", accountUUID)
              .setParameter(":email", email)
              .prepareBatchQuery(SQLDeviceDao.CREATE_DEVICE_QUERY)
              .setParameter(":device_uuid", device.getUuid())
              .setParameter(":account_uuid", accountUUID)
              .setParameter(":ip_address", device.getIp())
              .setParameter(":platform", device.getPlatform())
              .setParameter(":public_key", device.getPublicKey())
              .prepareBatchQuery(SQLDeviceDao.INSERT_DEVICE_VERIFY_CODE_QUERY)
              .setParameter(":device_uuid", device.getUuid())
              .setParameter(":account_uuid", accountUUID)
              .setParameter(":verify_code", verifyCode)
              .executeUpdate()) {
                return verifyCode;
            } else {
                throw new PwCryptException("Unexpected affected row count");
            }
        } catch(DBUtilsException e) {
            throw new PwCryptException("Error creating new account", e);
        }
    }
}
