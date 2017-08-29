package com.majoinen.d.pwcrypt.tracker.util;

/**
 * Path namespace.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class Path {
    public static class Web {
        // Route for registering an account
        public static final String REGISTER =
          "/register/:email/:device_uuid/:public_key/";

        // Route for logging in on a new device with existing account
        public static final String LOGIN =
          "/login/:email/:device_uuid/:public_key/";

        // Route for verifying device after login
        public static final String VERIFY_DEVICE =
          "/verify/:code/";

        // Route for listing all devices associated with account
        public static final String LIST_ALL_DEVICES =
          "/list/:account_uuid/:device_uuid/";
    }
}