package com.majoinen.d.pwcrypt.tracker;

import com.majoinen.d.database.DatabaseController;
import com.majoinen.d.database.DatabaseControllerFactory;
import com.majoinen.d.database.exception.DBUtilsException;
import com.majoinen.d.pwcrypt.tracker.account.AccountController;
import com.majoinen.d.pwcrypt.tracker.account.SQLAccountDao;
import com.majoinen.d.pwcrypt.tracker.util.Filters;
import com.majoinen.d.pwcrypt.tracker.util.Path;

import static spark.Spark.*;

/**
 * PwCryptTracker simply keeps track of accounts device IP addresses when using
 * auto peer discovery in PwCrypt.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class PwCryptTracker {
    public static void main(String[] args) throws DBUtilsException {
        DatabaseController databaseController =
          DatabaseControllerFactory.getController("pwcrypt-tracker");
        databaseController.init();

        AccountController accountController =
          new AccountController(SQLAccountDao.getInstance(databaseController));

        // Configure Spark
        port(4567);

        // Set up before-filters
        before("*", Filters.addTrailingSlashes);

        // Add routes
        get(Path.Web.REGISTER, accountController.register);

        // Set up after-filters
        after("*", Filters.addGzipHeader);
    }
}
