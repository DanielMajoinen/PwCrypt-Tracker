package com.majoinen.d.pwcrypt.tracker.device;

import com.google.gson.Gson;
import com.majoinen.d.encryption.exception.EncryptionUtilsException;
import com.majoinen.d.encryption.pkc.PKCUtils;
import com.majoinen.d.pwcrypt.tracker.account.SQLAccountDao;
import com.majoinen.d.pwcrypt.tracker.log.LogManager;
import com.majoinen.d.pwcrypt.tracker.log.Logger;
import com.majoinen.d.pwcrypt.tracker.spark.ResponseMessage;
import com.majoinen.d.pwcrypt.tracker.spark.SignedJSON;
import com.majoinen.d.pwcrypt.tracker.util.Path;
import spark.Request;
import spark.Response;
import spark.Route;

import java.security.PublicKey;

import static com.majoinen.d.pwcrypt.tracker.spark.SparkManager.error400;
import static spark.Spark.post;

/**
 * @author Daniel Majoinen
 * @version 1.0, 5/7/17
 */
public class DeviceController {

    private static final Logger LOGGER =
      LogManager.getLogger(DeviceController.class);

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static final Gson GSON = new Gson();

    private SQLDeviceDao deviceDao;
    private SQLAccountDao accountDao;

    public DeviceController(SQLDeviceDao deviceDao, SQLAccountDao accountDao) {
        this.deviceDao = deviceDao;
        this.accountDao = accountDao;
    }

    public void createRoutes() {
        post(Path.Web.VERIFY, verify, GSON::toJson);
        post(Path.Web.NEW_DEVICE, newDevice, GSON::toJson);
        post(Path.Web.LIST_ALL_DEVICES, list, GSON::toJson);
    }

    /**
     * Attempt to verify an account with the following steps:
     * 1. Verify signature
     * 2. Attempt to verify device
     */
    private Route verify = (Request request, Response response) -> {
        LOGGER.debug("Received verify request from: " + request.ip());
        SignedJSON signedJSON = GSON.fromJson(request.body(), SignedJSON.class);
        DeviceVerificationRequest verificationRequest = GSON.fromJson(signedJSON
          .getOriginal(), DeviceVerificationRequest.class);
        // Get variables from request
        String email = verificationRequest.getEmail();
        String deviceUUID = verificationRequest.getDeviceUUID();
        String accountUUID = accountDao.getAccountUUID(email);
        if(accountUUID == null)
            return error400(response, "Account error");
        // Verify signature
        LOGGER.debug("Verifying signature");
        if(!verifySignedJSON(accountUUID, deviceUUID, signedJSON))
            return error400(response, "Verifying signature failed");
        // Attempt to verify device
        LOGGER.debug("Attempting to verify device");
        if(!deviceDao.verifyDevice(verificationRequest.getCode()))
            return error400(response, "Failed to verify device");
        // Successfully verified device
        return new ResponseMessage("Successfully verified");
    };

    /**
     * A login attempt was made from a new device:
     * 1. Verify signature
     * 2. Add device to database
     * 3. Send verification email
     */
    private Route newDevice = (Request request, Response response) -> {
        SignedJSON signedJSON = GSON.fromJson(request.body(), SignedJSON.class);
        NewDeviceRequest newDeviceRequest = GSON.fromJson(signedJSON
          .getOriginal(), NewDeviceRequest.class);
        // Get variables from request
        String code;
        String email = newDeviceRequest.getEmail();
        String deviceUUID = newDeviceRequest.getDeviceUUID();
        String accountUUID = accountDao.getAccountUUID(email);
        String encodedKey = newDeviceRequest.getPublicKey();
        LOGGER.debug("Received new-device request from: " +
          accountUUID +" - "+ deviceUUID +" - "+request.ip());
        // Verify signature
        LOGGER.debug("Verifying signature");
        if(!verifySignedJSON(accountUUID, deviceUUID, signedJSON))
            return error400(response, "Verifying signature failed");
        // Verify device does not exist already
        boolean deviceExists = deviceDao.deviceExists(accountUUID, deviceUUID);
        // If device exists but is not verified - get code
        if(deviceExists && !deviceDao.isVerified(accountUUID, deviceUUID)) {
            LOGGER.debug("Device exists but is not verified");
            code = deviceDao.getVerifyCode(accountUUID, deviceUUID);
            // If device does not exist - create it and get code
        } else if(!deviceExists) {
            LOGGER.debug("Device does not exist");
            code = deviceDao.addDevice(accountUUID, new Device(deviceUUID,
              request.ip(), request.userAgent(), encodedKey));
        }
        LOGGER.debug("Emailing verification code");
        // TODO: Uncomment before deploy - Prevents emails being sent
        // EmailController.sendRegisterEmail(email, deviceUUID, code);
        return new ResponseMessage("Verification code has been sent");
    };

    /**
     * Provide a list of all other devices with the following steps:
     * 1. Verify signature
     * 2. Get device list
     */
    private Route list = (Request request, Response response) -> {
        SignedJSON signedJSON = GSON.fromJson(request.body(), SignedJSON.class);
        ListDeviceRequest listDeviceRequest = GSON.fromJson(signedJSON
          .getOriginal(), ListDeviceRequest.class);
        // Get variables from request
        String accountUUID = listDeviceRequest.getAccountUUID();
        String deviceUUID = listDeviceRequest.getDeviceUUID();
        LOGGER.debug("Received new-device request from: " +
          accountUUID +" - "+ deviceUUID +" - "+request.ip());
        // Verify signature
        LOGGER.debug("Verifying signature");
        if(!verifySignedJSON(accountUUID, deviceUUID, signedJSON))
            return error400(response, "Verifying signature failed");
        // Get all devices
        return deviceDao.listAllDevices(accountUUID, deviceUUID);
    };

    /**
     * Verify a signed device verification request.
     *
     * @param accountUUID The account UUID of the user.
     * @param deviceUUID The UUID of the users device.
     * @param signedJSON SignedJSON received from user.
     * @return True if the verification is successful, or false otherwise.
     */
    private boolean verifySignedJSON(String accountUUID, String deviceUUID,
      SignedJSON signedJSON) {
        try {
            PublicKey publicKey = getUsersPublicKey(accountUUID, deviceUUID);
            if(publicKey == null)
                return false;
            // Verify signature
            return PKCUtils.verifyBase64Signature(SIGNATURE_ALGORITHM,
              publicKey, signedJSON.getSignature(), signedJSON.getOriginal());
        } catch(EncryptionUtilsException e) {
            LOGGER.error("Error verifying signature of " +
              "DeviceVerificationRequest", e);
            return false;
        }
    }

    /**
     * Get a specified users public key, used when verifying a signed request.
     *
     * @param accountUUID The account UUID of the user.
     * @param deviceUUID The UUID of the device the user used to send the
     * request.
     * @return The users device specific public key.
     */
    private PublicKey getUsersPublicKey(String accountUUID, String deviceUUID) {
        String encodedKey = deviceDao.getPublicKey(accountUUID, deviceUUID);
        if(accountUUID == null || encodedKey == null)
            return null;
        return deserializePublicKey(encodedKey);
    }

    /**
     * Deserialize encoded public key, providing exception handling.
     *
     * @param encodedKey The encoded public key.
     * @return The public key deserialized.
     */
    private PublicKey deserializePublicKey(String encodedKey) {
        try {
            return PKCUtils.deserializeRSAPublicKey(encodedKey);
        } catch(EncryptionUtilsException e) {
            LOGGER.error("Error deserializing public key", e);
            return null;
        }
    }
}
