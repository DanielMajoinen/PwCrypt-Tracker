package com.majoinen.d.pwcrypt.tracker.device;

import com.google.gson.Gson;
import com.majoinen.d.pwcrypt.tracker.account.AccountController;
import com.majoinen.d.pwcrypt.tracker.log.LogManager;
import com.majoinen.d.pwcrypt.tracker.log.Logger;
import com.majoinen.d.pwcrypt.tracker.util.Path;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.post;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class DeviceController {

    private static final Logger LOGGER =
      LogManager.getLogger(DeviceController.class);

    private static final Gson GSON = new Gson();

    private SQLDeviceDao deviceDao;

    public DeviceController(SQLDeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public void createRoutes() {
        post(Path.Web.VERIFY, verify, GSON::toJson);
    }

    private Route verify = (Request request, Response response) -> {
        return null;
    }
}
