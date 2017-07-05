package com.majoinen.d.app;

import com.majoinen.d.util.Filters;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.port;

/**
 * PwCrypt-Peer-Tracker simply keeps track of device IP addresses when using
 * auto peer discovery in PwCrypt.
 *
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class PwCrypt {
    public static void main(String[] args) {
        // Configure Spark
        port(4567);

        // Set up before-filters
        before("*", Filters.addTrailingSlashes);

        //TODO: Add routes

        // Set up after-filters
        after("*", Filters.addGzipHeader);
    }
}
