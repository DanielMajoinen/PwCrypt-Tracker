package com.majoinen.d.pwcrypt.tracker.device;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class DeviceController {

    private SQLDeviceDao deviceDao;

    public DeviceController(SQLDeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public void createRoutes() {

    }
}
