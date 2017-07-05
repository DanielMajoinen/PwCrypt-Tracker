package com.majoinen.d.pwcrypt.tracker.app;

import com.majoinen.d.pwcrypt.tracker.device.DeviceController;
import com.majoinen.d.pwcrypt.tracker.util.Path;
import com.majoinen.d.pwcrypt.tracker.util.Filters;

import static spark.Spark.*;

/**
 * PwCrypt-Peer-Tracker simply keeps track of device IP addresses when using
 * auto peer discovery in PwCrypt.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class PwCrypt {
    public static void main(String[] args) {
        // Create dependencies
        DeviceController deviceController = new DeviceController();

        // Configure Spark
        port(4567);

        // Set up before-filters
        before("*", Filters.addTrailingSlashes);

        // Add routes
        get(Path.Web.LIST_DEVICES, deviceController.fetchAllDevices);

        // Set up after-filters
        after("*", Filters.addGzipHeader);
    }
}
