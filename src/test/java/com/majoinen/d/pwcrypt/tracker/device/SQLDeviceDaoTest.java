package com.majoinen.d.pwcrypt.tracker.device;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.pwcrypt.tracker.TestDatabaseManager;
import com.majoinen.d.pwcrypt.tracker.account.SQLAccountDao;
import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public class SQLDeviceDaoTest {

    private static final String EXISTING_ACC_UUID =
      "92a37290-728a-49f2-9589-57378acb3adc";

    private static final String EXISTING_EMAIL = "existing@domain.com";

    private static final String EXISTING_DEV_UUID =
      "d1ae9719-6001-486c-bc10-2b89b43f26ee";

    private static final String EXISTING_DEV_IP = "192.168.0.1";

    private static final String EXISTING_DEV_PLATFORM = "Desktop";

    private static final String EXISTING_DEV_PUBLIC_KEY = "pubkey1";

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
}