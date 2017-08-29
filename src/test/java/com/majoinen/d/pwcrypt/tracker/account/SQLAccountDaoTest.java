package com.majoinen.d.pwcrypt.tracker.account;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.pwcrypt.tracker.TestDatabaseManager;
import com.majoinen.d.pwcrypt.tracker.device.Device;
import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Majoinen
 * @version 1.0, 29/8/17
 */
public class SQLAccountDaoTest {

    private static final String NEW_EMAIL = "test@domain.com";

    private static final String NEW_DEV_UUID =
      "f0658a55-660d-4f53-979c-411e75271ed0";

    private static final String NEW_DEV_IP = "192.168.0.1";

    private static final String NEW_DEV_PLATFORM = "Desktop";

    private static final String NEW_DEV_PUBLIC_KEY = "pubkey";

    private static final String EXISTING_EMAIL = "existing@domain.com";

    private static final String EXISTING_ACC_UUID =
      "92a37290-728a-49f2-9589-57378acb3adc";

    private DatabaseController databaseController;
    private SQLAccountDao accountDao;

    @Before
    public void beforeEachTest() throws Exception {
        databaseController = TestDatabaseManager.initDatabaseController();
        accountDao = SQLAccountDao.getInstance(databaseController);

        databaseController.prepareQuery(SQLAccountDao.CREATE_ACCOUNT_QUERY)
          .setParameter(":account_uuid", EXISTING_ACC_UUID)
          .setParameter(":email", EXISTING_EMAIL)
          .executeUpdate();
    }

    @After
    public void afterEachTest() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
    }

    @Test
    public void getExistingInstance() throws Exception {
        SQLAccountDao duplicateAccountDao =
          SQLAccountDao.getInstance(databaseController);
        assertTrue(accountDao.equals(duplicateAccountDao));
    }

    @Test
    public void accountExists() throws Exception {
        assertTrue(accountDao.accountExists(EXISTING_EMAIL));
    }

    @Test
    public void accountDoesNotExist() throws Exception {
        assertTrue(!accountDao.accountExists(NEW_EMAIL));
    }

    @Test(expected = PwCryptException.class)
    public void accountExistsThrowException() throws Exception {
        TestDatabaseManager.deleteTestDatabase();
        assertTrue(!accountDao.accountExists(NEW_EMAIL));
    }

    @Test
    public void createValidAccount() throws Exception {
        Device device = new Device(NEW_DEV_UUID, NEW_DEV_IP,
          NEW_DEV_PLATFORM, NEW_DEV_PUBLIC_KEY);
        String[] codes = accountDao.createAccount(NEW_EMAIL, device);
        assertTrue(codes[SQLAccountDao.ACCOUNT_CODE_INDEX].length() ==
          SQLAccountDao.VERIFY_CODE_LENGTH);
        assertTrue(codes[SQLAccountDao.DEVICE_CODE_INDEX].length() ==
          SQLAccountDao.VERIFY_CODE_LENGTH);
    }

    @Test(expected = PwCryptException.class)
    public void createInvalidAccount() throws Exception {
        Device device = new Device(NEW_DEV_UUID, NEW_DEV_IP,
          NEW_DEV_PLATFORM, NEW_DEV_PUBLIC_KEY);
        accountDao.createAccount(NEW_EMAIL, device);
        accountDao.createAccount(NEW_EMAIL, device);
    }
}