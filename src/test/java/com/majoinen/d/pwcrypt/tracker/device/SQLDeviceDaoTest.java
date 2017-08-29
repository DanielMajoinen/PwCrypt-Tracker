package com.majoinen.d.pwcrypt.tracker.device;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.pwcrypt.tracker.TestDatabaseManager;
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

    private static final String EXISTING_DEV_UUID =
      "d1ae9719-6001-486c-bc10-2b89b43f26ee";

    private static final String NEW_DEV_UUID =
      "f0658a55-660d-4f53-979c-411e75271ed0";

    private static final String NEW_DEV_IP = "192.168.0.1";

    private static final String NEW_DEV_PLATFORM = "Desktop";

    private static final String NEW_DEV_PUBLIC_KEY = "pubkey";

    private static final Device NEW_DEVICE = new Device(NEW_DEV_UUID,
      NEW_DEV_IP, NEW_DEV_PLATFORM, NEW_DEV_PUBLIC_KEY);

    private DatabaseController databaseController;
    private SQLDeviceDao deviceDao;

    @Before
    public void beforeEachTest() throws Exception {
        databaseController = TestDatabaseManager.initDatabaseController();
        deviceDao = SQLDeviceDao.getInstance(databaseController);
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

    @Test
    public void listAllDevicesEmpty() throws Exception {
        List<Device> deviceList = deviceDao.listAllDevices(EXISTING_ACC_UUID,
          EXISTING_DEV_UUID);
        assertTrue(deviceList.isEmpty());
    }

    @Test
    public void listAllDevices() throws Exception {
        // TODO: Query to add new device
        List<Device> deviceList = deviceDao.listAllDevices(EXISTING_ACC_UUID,
          EXISTING_DEV_UUID);
        assertTrue(!deviceList.isEmpty());
        assertTrue(deviceList.size() == 1);
    }
}