package com.majoinen.d.pwcrypt.tracker.spark;

import com.majoinen.d.pwcrypt.tracker.log.LogManager;
import com.majoinen.d.pwcrypt.tracker.log.Logger;
import com.majoinen.d.pwcrypt.tracker.util.Filters;
import spark.Response;

import static spark.Spark.*;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public class SparkManager {

    private static final Logger LOGGER =
      LogManager.getLogger(SparkManager.class);

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

    /**
     * Return a response with a 400 error status and message to go with it.
     *
     * @param response Response responsible for sending data back to user.
     * @param message Message to supply response.
     * @return A ResponseMessage.
     */
    public static ResponseMessage error400(Response response, String message) {
        LOGGER.debug("Returning Error 400: " + message);
        response.status(400);
        return new ResponseMessage(message);
    }
}
