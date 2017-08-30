package com.majoinen.d.pwcrypt.tracker.account;

import com.google.gson.Gson;
import com.majoinen.d.encryption.exception.EncryptionUtilsException;
import com.majoinen.d.encryption.pkc.PKCUtils;
import com.majoinen.d.pwcrypt.tracker.device.Device;
import com.majoinen.d.pwcrypt.tracker.exception.PwCryptException;
import com.majoinen.d.pwcrypt.tracker.log.LogManager;
import com.majoinen.d.pwcrypt.tracker.log.Logger;
import com.majoinen.d.pwcrypt.tracker.mail.EmailController;
import com.majoinen.d.pwcrypt.tracker.spark.ResponseMessage;
import com.majoinen.d.pwcrypt.tracker.spark.SignedJSON;
import com.majoinen.d.pwcrypt.tracker.util.Path;
import spark.Request;
import spark.Response;
import spark.Route;

import java.security.PublicKey;

import static spark.Spark.post;

/**
 * @author Daniel Majoinen
 * @version 1.0, 29/8/17
 */
public class AccountController {

    private static final Logger LOGGER =
      LogManager.getLogger(AccountController.class);

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static final Gson GSON = new Gson();

    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void createRoutes() {
        post(Path.Web.REGISTER, register, GSON::toJson);
    }

    /**
     * Attempt to register an account with the following steps:
     * 1. Verify signature
     * 2. Verify email is not in use
     * 3. Attempt to create account
     * 4. Email user verification code
     */
    private Route register = (Request request, Response response) -> {
        LOGGER.debug("Received register request from: " + request.ip());
        SignedJSON signedJSON = GSON.fromJson(request.body(), SignedJSON.class);
        RegisterRequest registerRequest = GSON.fromJson(signedJSON.getOriginal(),
          RegisterRequest.class);
        // Verify signature
        LOGGER.debug("Verifying signature");
        if(!verifySignedRegisterRequest(registerRequest, signedJSON))
            return error400(response, "Verifying signature failed");
        // Verify email is not in use
        LOGGER.debug("Verifying email not in use");
        if(accountDao.accountExists(registerRequest.getEmail()))
            return error400(response, "Email already in use");
        // Attempt to create account
        LOGGER.debug("Registering account");
        if(!registerAccount(registerRequest, request.ip(), request.userAgent()))
            return error400(response, "Error creating account");
        // Successfully created account
        return new ResponseMessage("Successfully registered");
    };

    /**
     * Register an account using the supplied RegisterRequest and device
     * information.
     *
     * @param registerRequest Necessary registration information.
     * @param ip IP Address of device used to register.
     * @param platform Platform information of device used to register.
     * @return True if registration is successful.
     */
    private boolean registerAccount(RegisterRequest registerRequest,
      String ip, String platform) {
        try {
            String email = registerRequest.getEmail();
            String deviceUUID = registerRequest.getDeviceUUID();
            String publicKey = registerRequest.getPublicKey();
            String code = accountDao.createAccount(email,
              new Device(deviceUUID, ip, platform, publicKey));
            LOGGER.debug("Sending registration verification email: " + email);
            EmailController.sendRegisterEmail(email, deviceUUID, code);
            LOGGER.info("Successfully registered account: " + email);
            return true;
        } catch(PwCryptException e) {
            LOGGER.error("Error registering account", e);
            return false;
        }
    }

    /**
     * Verify a signed registration request.
     *
     * @param registerRequest The registration request, containing the public
     * key.
     * @param signedJSON SignedJSON received from user.
     * @return True if verification succeeds.
     */
    private boolean verifySignedRegisterRequest(RegisterRequest registerRequest, SignedJSON signedJSON) {
        try {
            // Deserialize public key
            PublicKey publicKey = PKCUtils.deserializeRSAPublicKey(
              registerRequest.getPublicKey());
            // Verify signature
            return PKCUtils.verifyBase64Signature(SIGNATURE_ALGORITHM,
              publicKey, signedJSON.getSignature(), signedJSON.getOriginal());
        } catch(EncryptionUtilsException e) {
            LOGGER.error("Error verifying signature of RegisterRequest", e);
            return false;
        }
    }

    /**
     * Return a response with a 400 error status and message to go with it.
     *
     * @param response Response responsible for sending data back to user.
     * @param message Message to supply response.
     * @return A ResponseMessage.
     */
    private ResponseMessage error400(Response response, String message) {
        LOGGER.debug("Returning Error 400: " + message);
        response.status(400);
        return new ResponseMessage(message);
    }
}
