package com.majoinen.d.pwcrypt.tracker.account;

import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import com.majoinen.d.encryption.pkc.PKCUtils;
import com.majoinen.d.encryption.utils.EncryptionKeyGenerator;
import com.majoinen.d.encryption.utils.Tools;
import com.majoinen.d.pwcrypt.tracker.spark.ResponseMessage;
import com.majoinen.d.pwcrypt.tracker.spark.SignedJSON;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.servlet.SparkApplication;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Majoinen
 * @version 1.0, 30/8/17
 */
public class AccountControllerTest {

    private static final String NEW_EMAIL = "dmajoinen@gmail.com";

    private static final String NEW_DEV_UUID =
      "f0658a55-660d-4f53-979c-411e75271ed0";

    private static final String NEW_DEV_IP = "192.168.0.1";

    private static final String NEW_DEV_PLATFORM = "Desktop";

    private static final String NEW_DEV_PUBLIC_KEY = "pubkey";

    private static final String EXISTING_ACC_UUID =
      "92a37290-728a-49f2-9589-57378acb3adc";

    private static final Gson GSON = new Gson();

    public static class AccountControllerTestSparkApp implements SparkApplication {

        @Mock
        private static AccountDao accountDao;

        private AccountController accountController;

        @Override
        public void init() {
            MockitoAnnotations.initMocks(this);
            accountController = new AccountController(accountDao);
            accountController.createRoutes();
        }
    }

    @ClassRule
    public static SparkServer<AccountControllerTestSparkApp> testServer =
      new SparkServer<>(AccountControllerTestSparkApp.class, 4567);

    @Test
    public void registerAccountEmailExists() throws Exception {
        when(AccountControllerTestSparkApp.accountDao.accountExists(
          anyString())).thenReturn(true);

        // Convert to JSON and submit request
        HttpResponse httpResponse = executeNewRegisterRequest();
        // Get body and convert to response message
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(400, httpResponse.code());
        assertEquals(responseMessage.getMessage(), "Email already in use");
        assertNotNull(testServer.getApplication());
    }

    @Test
    public void registerValidAccount() throws Exception {
        when(AccountControllerTestSparkApp.accountDao.accountExists(
          anyString())).thenReturn(false);
        when(AccountControllerTestSparkApp.accountDao.createAccount(
          anyString(), any())).thenReturn(Tools.generateRandomString(SQLAccountDao.VERIFY_CODE_LENGTH, Tools.ALPHA_NUMERIC));

        // Convert to JSON and submit request
        HttpResponse httpResponse = executeNewRegisterRequest();
        // Get body and convert to response message
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(200, httpResponse.code());
        assertEquals(responseMessage.getMessage(), "Successfully registered");
        assertNotNull(testServer.getApplication());
    }

    private HttpResponse executeNewRegisterRequest() throws Exception {
        // Generate keypair
        KeyPair keyPair = EncryptionKeyGenerator.generateKeyPair("RSA", 2048);
        String publicKey = PKCUtils.serializeRSAPublicKey((RSAPublicKey)
          keyPair.getPublic());

        // Create new register request
        RegisterRequest registerRequest = new RegisterRequest(
          NEW_EMAIL, NEW_DEV_UUID, publicKey);
        String bodyOriginal = GSON.toJson(registerRequest);

        // Sign register request
        String bodySigned = PKCUtils.sign("SHA256withRSA",
          keyPair.getPrivate(), bodyOriginal.getBytes());

        // Create SignedJSON
        String bodyJSON = GSON.toJson(new SignedJSON(bodyOriginal, bodySigned));

        // Execute
        PostMethod post = testServer.post("/register/", bodyJSON, false);
        return testServer.execute(post);
    }
}