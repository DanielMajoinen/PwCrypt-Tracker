package com.majoinen.d.pwcrypt.tracker;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.DatabaseControllerFactory;

import java.io.File;

/**
 * @author Daniel Majoinen
 * @version 1.0, 29/8/17
 */
public class TestDatabaseManager {

    private static final String DB_NAME = "pwcrypt-tracker";

    public static DatabaseController initDatabaseController() throws Exception {
        DatabaseController databaseController =
          DatabaseControllerFactory.getController(DB_NAME);
        databaseController.init();
        return databaseController;
    }

    public static void deleteTestDatabase() throws Exception {
        if(new File("test/pwcrypt-tracker.db").exists()
          && !new File("test/pwcrypt-tracker.db").delete())
            throw new Exception("Failed deleting test db");
        if(new File("test/").exists()
          && !new File("test/").delete())
            throw new Exception("Failed deleting test db directory");
    }
}
