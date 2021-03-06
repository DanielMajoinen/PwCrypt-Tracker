package com.majoinen.d.pwcrypt.tracker.util;

import spark.Filter;
import spark.Request;
import spark.Response;

/**
 * Filters -- Taken from spark-basic-structure tutorial.
 * https://github.com/tipsy/spark-basic-structure
 */
public class Filters {

    // If a user manually manipulates paths and forgets to add
    // a trailing slash, redirect the user to the correct path
    public static Filter addTrailingSlashes = (Request request, Response response) -> {
        if (!request.pathInfo().endsWith("/")) {
            response.redirect(request.pathInfo() + "/");
        }
    };

    // Enable GZIP for all responses
    public static Filter addGzipHeader = (Request request, Response response) -> {
        response.header("Content-Encoding", "gzip");
    };

}
