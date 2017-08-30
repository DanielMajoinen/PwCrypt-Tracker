# PwCrypt-Tracker

[![Build Status](https://travis-ci.com/DanielMajoinen/PwCrypt-Tracker.svg?token=aBjt9HY25c6nESBMDy73&branch=develop)](https://travis-ci.com/DanielMajoinen/PwCrypt-Tracker)

Allow PwCrypt to find other devices via RESTful API.

It provides the necessary information for authorised devices to locate each other in order to begin peer-to-peer synchronisation. All information served as results is encrypted with each unique devices, account specific, public key. 

It is worth noting that the master password, even hashed, is never transmitted/stored. Authentication of an account is handled through email. Once authenticated, authorised devices will begin the p2p syncing process, after which the users password will be used for decryption locally.

## Routes

#### Register Account

When a new account is created:

    /register/:email/:device_uuid/:public_key/

Response:

    Success: 200 - Email is not in use, verify code sent
    Failure: 400 - Email already in use

An email with a verify code, encrypted with the public key, will be sent. 

#### Verify Email

After registration the account must be verified with a code provided in an email. The appropriate private key will be used to decrypt the code.

    /verify-email/:verify_code/

Response:

    Success: 200 - The accounts UUID
    Failure: 401 - Incorrect code

#### Login New Device:

If an account already exists, but has never been synced to current device:

    /login/:email/:device_uuid/:public_key/

Response:

    Success: 200 - Email exists, send verify code
    Failure: 400 - Email does not exist

An email with a verify code, encrypted with the public key, will be sent. 

#### Verify New Device

After logging in on a new device, the device/account must be verified with a code provided in an email. The appropriate private key will be used to decrypt the code.

    /verify-device/:decrypted_code/

Response:

    Success: 200 - The accounts UUID
    Failure: 401 - Incorrect code

#### List All Devices

When a device wants to find other devices associated with the account in order to sync.

    /list/:account_uuid/:device_uuid/

Response:

    Success: 200 - List of device info
    Failure: 401 - Incorrect account UUID or device UUID
