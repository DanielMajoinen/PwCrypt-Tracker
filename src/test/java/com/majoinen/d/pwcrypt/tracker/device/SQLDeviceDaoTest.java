package com.majoinen.d.pwcrypt.tracker.device;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.encryption.pkc.PKCUtils;
import com.majoinen.d.pwcrypt.tracker.TestDatabaseManager;
import com.majoinen.d.pwcrypt.tracker.account.SQLAccountDao;
import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public class SQLDeviceDaoTest {

    private static final String SELECT_VERIFY_CODE =
      "SELECT verify_code FROM device_verify_code WHERE " +
        "account_uuid = :account_uuid AND device_uuid = :device_uuid";

    private static final String VERIFY_DEVICE_QUERY =
      "UPDATE device SET verified = 1 " +
        "WHERE account_uuid = :account_uuid " +
        "AND device_uuid = :device_uuid";

    private static final String EXISTING_ACC_UUID =
      "92a37290-728a-49f2-9589-57378acb3adc";

    private static final String EXISTING_EMAIL = "existing@domain.com";

    private static final String EXISTING_DEV_UUID =
      "d1ae9719-6001-486c-bc10-2b89b43f26ee";

    private static final String EXISTING_DEV_IP = "192.168.0.1";

    private static final String EXISTING_DEV_PLATFORM = "Desktop";

    private static final String EXISTING_DEV_PUBLIC_KEY =
      "AJPnZFlGYOjKvhmO7OisPb6PWro7UbYaI/MA+h5mTucFoAxnTW0JlBorE6KBYge1vwhGVV" +
        "VeDw8s6/gz5KV1dMD0dEcWpWz6ATSpUlwov9WX9RtV1R6xyMeMHXOnjjBXDqw/aIFI85" +
        "HNlP+v+/72mp7KDs7Q1qxPMoFFLcVoocQzwY/KsQE6uBFXe6Wc48ozxqOdBeR6D9JCtI" +
        "n6FZ8gbtuoEIwcXqFll2zKLlpO+7zu7Few+zcFGjg/P47mVFNtPAgcWzAE/A4FeaNt09" +
        "1qFaWsLGjoI71OJLmLRxaMYNYZjXW8OOnZrAgIpnB2y9Hc2WhuLkYbxND8+oSzlElHTs" +
        "8=:AQAB";

    private static final String EXISTING_DEV_VERIFY_CODE = "12345";

    private static final String NEW_DEV_UUID =
      "f0658a55-660d-4f53-979c-411e75271ed0";

    private static final String NEW_DEV_IP = "192.168.0.2";

    private static final String NEW_DEV_PLATFORM = "Desktop";

    private static final String NEW_DEV_PUBLIC_KEY = "pubkey2";

    private static final Device NEW_DEVICE = new Device(NEW_DEV_UUID,
      NEW_DEV_IP, NEW_DEV_PLATFORM, NEW_DEV_PUBLIC_KEY);

    private DatabaseController databaseController;
    private SQLDeviceDao deviceDao;

    @Before
    public void beforeEachTest() throws Exception {
        databaseController = TestDatabaseManager.initDatabaseController();
        deviceDao = SQLDeviceDao.getInstance(databaseController);

        databaseController.prepareQuery(SQLAccountDao.CREATE_ACCOUNT_QUERY)
          .setParameter(":account_uuid", EXISTING_ACC_UUID)
          .setParameter(":email", EXISTING_EMAIL)
          .executeUpdate();

        databaseController.prepareQuery(SQLDeviceDao.CREATE_DEVICE_QUERY)
          .setParameter(":device_uuid", EXISTING_DEV_UUID)
          .setParameter(":account_uuid", EXISTING_ACC_UUID)
          .setParameter(":ip_address", EXISTING_DEV_IP)
          .setParameter(":platform", EXISTING_DEV_PLATFORM)
          .setParameter(":public_key", EXISTING_DEV_PUBLIC_KEY)
          .executeUpdate();

        databaseController
          .prepareQuery(SQLDeviceDao.INSERT_DEVICE_VERIFY_CODE_QUERY)
          .setParameter(":device_uuid", EXISTING_DEV_UUID)
          .setParameter(":account_uuid", EXISTING_ACC_UUID)
          .setParameter(":verify_code", EXISTING_DEV_VERIFY_CODE)
          .executeUpdate();

        databaseController
          .prepareQuery(VERIFY_DEVICE_QUERY)
          .setParameter(":device_uuid", EXISTING_DEV_UUID)
          .setParameter(":account_uuid", EXISTING_ACC_UUID)
          .executeUpdate();
    }

    @After
    public void afterEachTest() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
    }

    @Test
    public void getExistingInstance() throws Exception {
        SQLDeviceDao duplicateDeviceDao =
          SQLDeviceDao.getInstance(databaseController);
        assertTrue(deviceDao.equals(duplicateDeviceDao));
    }

    @Test
    public void deviceExists() throws Exception {
        assertTrue(deviceDao.deviceExists(EXISTING_ACC_UUID,
          EXISTING_DEV_UUID));
    }

    @Test
    public void deviceDoesNotExist() throws Exception {
        assertTrue(!deviceDao.deviceExists(EXISTING_ACC_UUID, NEW_DEV_UUID));
    }

    @Test(expected = PwCryptException.class)
    public void deviceExistsThrowsException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        deviceDao.deviceExists(EXISTING_ACC_UUID, EXISTING_DEV_UUID);
    }

    @Test
    public void addValidDevice() throws Exception {
        String code = deviceDao.addDevice(EXISTING_ACC_UUID, NEW_DEVICE);
        assertTrue(code.length() == SQLDeviceDao.VERIFY_CODE_LENGTH);
    }

    @Test(expected = PwCryptException.class)
    public void addDeviceThrowsException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        deviceDao.addDevice(EXISTING_ACC_UUID, NEW_DEVICE);
    }

    @Test
    public void verifyDevice() throws Exception {
        assertTrue(deviceDao.verifyDevice(EXISTING_DEV_VERIFY_CODE));
    }

    @Test(expected = PwCryptException.class)
    public void verifyDeviceThrowsException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        assertTrue(deviceDao.verifyDevice(EXISTING_DEV_VERIFY_CODE));
    }

    @Test
    public void listAllDevicesEmpty() throws Exception {
        List<Device> deviceList = deviceDao.listAllDevices(EXISTING_ACC_UUID,
          EXISTING_DEV_UUID);
        assertTrue(deviceList.isEmpty());
    }

    @Test
    public void listAllDevices() throws Exception {
        databaseController.prepareQuery(SQLDeviceDao.CREATE_DEVICE_QUERY)
          .setParameter(":device_uuid", "97902f80-c1ea-44af-b2ef-361793e36f51")
          .setParameter(":account_uuid", EXISTING_ACC_UUID)
          .setParameter(":ip_address", "192.168.0.3")
          .setParameter(":platform", "Mobile")
          .setParameter(":public_key", "pubKey3")
          .executeUpdate();
        List<Device> deviceList =
          deviceDao.listAllDevices(EXISTING_ACC_UUID, EXISTING_DEV_UUID);
        assertTrue(!deviceList.isEmpty());
        assertTrue(deviceList.size() == 1);
    }

    @Test(expected = PwCryptException.class)
    public void listAllDevicesThrowsException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        deviceDao.listAllDevices(EXISTING_ACC_UUID, EXISTING_DEV_UUID);
    }

    @Test
    public void getValidPublicKey() throws Exception {
        String publicKey = deviceDao.getPublicKey(EXISTING_ACC_UUID,
          EXISTING_DEV_UUID);
        assertNotNull(publicKey);
        assertNotNull(PKCUtils.deserializeRSAPublicKey(publicKey));
    }

    @Test
    public void getNonExistingPublicKey() throws Exception {
        String publicKey = deviceDao.getPublicKey(NEW_DEV_UUID, NEW_DEV_UUID);
        assertNull(publicKey);
    }

    @Test(expected = PwCryptException.class)
    public void getPublicKeyThrowsException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        deviceDao.getPublicKey(EXISTING_ACC_UUID, EXISTING_DEV_UUID);
    }

    @Test
    public void isVerified() throws Exception {
        assertTrue(deviceDao.isVerified(EXISTING_ACC_UUID, EXISTING_DEV_UUID));
    }

    @Test
    public void isNotVerified() throws Exception {
        assertFalse(deviceDao.isVerified(NEW_DEV_UUID, NEW_DEV_UUID));
    }

    @Test(expected = PwCryptException.class)
    public void isVerifiedThrowsException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        deviceDao.isVerified(EXISTING_ACC_UUID, EXISTING_DEV_UUID);
    }

    @Test
    public void getVerifyCode() throws Exception {
        assertTrue(deviceDao.getVerifyCode(EXISTING_ACC_UUID,
          EXISTING_DEV_UUID).equals(EXISTING_DEV_VERIFY_CODE));
    }

    @Test(expected = PwCryptException.class)
    public void getVerifyCodeThrowsException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        deviceDao.getVerifyCode(EXISTING_ACC_UUID, EXISTING_DEV_UUID);
    }

    @Test(expected = PwCryptException.class)
    public void getVerifyCodeThrowsNullPointerException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        deviceDao.getVerifyCode(NEW_DEV_UUID, NEW_DEV_UUID);
    }
}