package com.majoinen.d.pwcrypt.tracker.account;

import java.util.UUID;

/**
 * Model class for an Account.
 *
 * @author Daniel Majoinen
 * @version 1.0, 28/8/17
 */
public class Account {

    private UUID uuid;

    private String email;

    public Account(String uuid, String email) {
        this.uuid = UUID.fromString(uuid);
        this.email = email;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
