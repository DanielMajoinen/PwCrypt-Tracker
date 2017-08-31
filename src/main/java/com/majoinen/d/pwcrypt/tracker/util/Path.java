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
        public static final String REGISTER = "/register/";

        // Route for verifying device after login
        public static final String VERIFY = "/verify/";

        // Route for logging in on a new device with existing account
        public static final String NEW_DEVICE = "/new-device/";

        // Route for listing all devices associated with account
        public static final String LIST_ALL_DEVICES = "/list/";
    }
}