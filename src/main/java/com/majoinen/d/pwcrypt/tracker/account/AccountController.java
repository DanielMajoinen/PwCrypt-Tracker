package com.majoinen.d.pwcrypt.tracker.account;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * @author Daniel Majoinen
 * @version 1.0, 29/8/17
 */
public class AccountController {

    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Route register = (Request request, Response response) -> {
        return null;
    };

}
