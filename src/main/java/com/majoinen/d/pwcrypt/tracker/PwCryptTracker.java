package com.majoinen.d.pwcrypt.tracker;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.DatabaseControllerFactory;
import com.majoinen.d.pwcrypt.tracker.account.AccountController;
import com.majoinen.d.pwcrypt.tracker.account.SQLAccountDao;
import com.majoinen.d.pwcrypt.tracker.device.DeviceController;
import com.majoinen.d.pwcrypt.tracker.device.SQLDeviceDao;
import com.majoinen.d.pwcrypt.tracker.spark.SparkManager;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class PwCryptTracker {

    private static final String DB_NAME = "pwcrypt-tracker";

    public static void main(String[] args) throws Exception {
        // Initialise spark
        SparkManager.initSpark();
        // Instantiate controllers
        DatabaseController databaseController =
          DatabaseControllerFactory.getController(DB_NAME);
        AccountController accountController = new AccountController(
          SQLAccountDao.getInstance(databaseController));
        DeviceController deviceController = new DeviceController(
          SQLDeviceDao.getInstance(databaseController),
          SQLAccountDao.getInstance(databaseController));
        // Initialise database and create routes
        databaseController.init();
        accountController.createRoutes();
        deviceController.createRoutes();
    }
}
