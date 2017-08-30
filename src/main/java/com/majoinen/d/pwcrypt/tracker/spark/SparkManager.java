package com.majoinen.d.pwcrypt.tracker.spark;

import com.majoinen.d.pwcrypt.tracker.util.Filters;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.port;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public class SparkManager {

    // TODO: Move to config file
    private static final int PORT = 4567;

    public static void initSpark() {
        configureSpark();
        prepareBeforeFilters();
        prepareAfterFilters();
    }

    // Configure Spark
    private static void configureSpark() {
        port(PORT);
    }

    private static void prepareBeforeFilters() {
        before("*", Filters.addTrailingSlashes);
    }

    private static void prepareAfterFilters() {
        after("*", Filters.addGzipHeader);
    }
}
