package com.majoinen.d.pwcrypt.tracker.device;

import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.majoinen.d.encryption.pkc.PKCUtils;
import com.majoinen.d.pwcrypt.tracker.account.AccountDao;
import com.majoinen.d.pwcrypt.tracker.spark.ResponseMessage;
import com.majoinen.d.pwcrypt.tracker.spark.SignedJSON;
import com.majoinen.d.pwcrypt.tracker.util.Path;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import spark.servlet.SparkApplication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Majoinen
 * @version 1.0, 31/8/17
 */
public class DeviceControllerTest {

    private static final String EXISTING_ACC_UUID =
      "84a5938b-ca93-438f-8cb4-9f1b98dc3392";

    private static final String EXISTING_DEV_UUID =
      "881f7e16-0b18-4ba9-8abb-7fad0a4a46f4";

    private static final String NEW_DEV_EMAIL = "email@domain.com";

    private static final String NEW_DEV_UUID =
      "f0658a55-660d-4f53-979c-411e75271ed0";

    private static final String NEW_DEV_IP = "192.168.0.1";

    private static final String NEW_DEV_PLATFORM = "Desktop";

    private static final String NEW_DEV_VERIFY_CODE = "12345";

    private static final String DEV_PRIVATE_KEY =
      "AIB1xp2gBr9luTWVxQtIsPWh/Lskz6G7RudDPTKJFJEQavIzHAQ6HlK3DqHaFDdwkt91L4" +
        "fHgwKzzH5dAlFhferWsLIJOJfiHW5St/YjATrRqFSgk0fLCAc/OIgmCMoS+7VULI2J1N" +
        "yrO0T/IGK/MtKXwR2cEhAev87IfsGx+Zqpvul6Gt1r8m3VtTr0kuJtO5wS0RjeQoU01A" +
        "w/bpg/r+vZ4uvMClukvnBTY3vaMeYtsyMWZZNqxe0wVHjprEr46PcAaH0P4XuLDSBO5k" +
        "pyMP/0N3biUjXaMCqQihLv5H59l1bWaQs+cUBsBpJMnwS0uBC44A7mN8W9/XGCS97fQO" +
        "M=:BC1QNwHkFkclfZOQI6zKbEJHKIKq6bRgOtbZ35HLhGgHQjbdO1n0cHYZWLPxolfbv" +
        "r5dvEmFfgoApT86+xBzerZeanuYRoAIuvSfnLZIrPPySEYsY9I7QZkRqoOqF4AEhUHmb" +
        "PfFXyOGOtkc4XkcGvLqSZ8wiyukywGD9aJm3luIy/qR+8jqEDQ+wlie1KSZIUWJNHAFf" +
        "ubeT5snkBmKtAnH4oRGdEKpaAHFSep+oOY1QlMptvI0TJXn5TXXa6/xIEwzkjfm89niv" +
        "RWldj6q9a6ByH/iESRw7egm7G5rwS4PXKu/oK5Go6B2Rz8VD0gqJePeSim/0cBY4nz80" +
        "uawEQ==";

    private static final String DEV_PUBLIC_KEY =
      "AIB1xp2gBr9luTWVxQtIsPWh/Lskz6G7RudDPTKJFJEQavIzHAQ6HlK3DqHaFDdwkt91L4" +
        "fHgwKzzH5dAlFhferWsLIJOJfiHW5St/YjATrRqFSgk0fLCAc/OIgmCMoS+7VULI2J1N" +
        "yrO0T/IGK/MtKXwR2cEhAev87IfsGx+Zqpvul6Gt1r8m3VtTr0kuJtO5wS0RjeQoU01A" +
        "w/bpg/r+vZ4uvMClukvnBTY3vaMeYtsyMWZZNqxe0wVHjprEr46PcAaH0P4XuLDSBO5k" +
        "pyMP/0N3biUjXaMCqQihLv5H59l1bWaQs+cUBsBpJMnwS0uBC44A7mN8W9/XGCS97fQO" +
        "M=:AQAB";

    private static final Gson GSON = new Gson();

    public static class DeviceControllerTestSparkApp implements SparkApplication {

        @Mock
        private static DeviceDao deviceDao;

        @Mock
        private static AccountDao accountDao;

        private DeviceController deviceController;

        @Override
        public void init() {
            MockitoAnnotations.initMocks(this);
            deviceController = new DeviceController(deviceDao, accountDao);
            deviceController.createRoutes();
        }
    }

    @ClassRule
    public static SparkServer<DeviceControllerTestSparkApp> testServer =
      new SparkServer<>(DeviceControllerTestSparkApp.class, 4567);

    @Test
    public void verifyNewDeviceNullAccountUUID() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(null);

        HttpResponse httpResponse = executeNewVerifyRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(400, httpResponse.code());
        assertEquals(responseMessage.getMessage(), "Account error");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void verifyNewDeviceFailedSignature() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(NEW_DEV_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(null);

        HttpResponse httpResponse = executeNewVerifyRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(400, httpResponse.code());
        assertEquals(responseMessage.getMessage(),
          "Verifying signature failed");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void verifyNewDeviceFailedVerification() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(NEW_DEV_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(DEV_PUBLIC_KEY);
        when(DeviceControllerTestSparkApp.deviceDao.verifyDevice(
          anyString())).thenReturn(false);

        HttpResponse httpResponse = executeNewVerifyRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(400, httpResponse.code());
        assertEquals(responseMessage.getMessage(), "Failed to verify device");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void verifyNewDeviceSuccess() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(NEW_DEV_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(DEV_PUBLIC_KEY);
        when(DeviceControllerTestSparkApp.deviceDao.verifyDevice(
          anyString())).thenReturn(true);

        HttpResponse httpResponse = executeNewVerifyRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(200, httpResponse.code());
        assertEquals(responseMessage.getMessage(), "Successfully verified");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void addNewDeviceFailedSignature() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(EXISTING_ACC_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(null);

        HttpResponse httpResponse = executeNewDeviceRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(400, httpResponse.code());
        assertEquals(responseMessage.getMessage(),
          "Verifying signature failed");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void addNewDeviceDeviceExistsNotVerified() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(EXISTING_ACC_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(DEV_PUBLIC_KEY);
        when(DeviceControllerTestSparkApp.deviceDao.deviceExists(
          anyString(), anyString())).thenReturn(true);
        when(DeviceControllerTestSparkApp.deviceDao.isVerified(
          anyString(), anyString())).thenReturn(false);
        when(DeviceControllerTestSparkApp.deviceDao.getVerifyCode(
          anyString(), anyString())).thenReturn(NEW_DEV_VERIFY_CODE);

        HttpResponse httpResponse = executeNewDeviceRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(200, httpResponse.code());
        assertEquals(responseMessage.getMessage(),
          "Verification code has been sent");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void addNewDeviceDeviceDoesNotExist() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(EXISTING_ACC_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(DEV_PUBLIC_KEY);
        when(DeviceControllerTestSparkApp.deviceDao.deviceExists(
          anyString(), anyString())).thenReturn(false);
        when(DeviceControllerTestSparkApp.deviceDao.addDevice(
          anyString(), any())).thenReturn(NEW_DEV_VERIFY_CODE);

        HttpResponse httpResponse = executeNewDeviceRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(200, httpResponse.code());
        assertEquals(responseMessage.getMessage(),
          "Verification code has been sent");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void listDeviceFailedSignature() throws Exception {
        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(EXISTING_ACC_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(null);

        HttpResponse httpResponse = executeListRequest();
        String body = new String(httpResponse.body());
        ResponseMessage responseMessage = GSON.fromJson(body,
          ResponseMessage.class);

        assertEquals(400, httpResponse.code());
        assertEquals(responseMessage.getMessage(),
          "Verifying signature failed");
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    @Test
    public void listDeviceSuccess() throws Exception {
        List<Device> devices = new ArrayList<>();
        devices.add(new Device(NEW_DEV_UUID, NEW_DEV_IP, NEW_DEV_PLATFORM,
          DEV_PUBLIC_KEY));

        when(DeviceControllerTestSparkApp.accountDao.getAccountUUID(
          anyString())).thenReturn(EXISTING_ACC_UUID);
        when(DeviceControllerTestSparkApp.deviceDao.getPublicKey(
          anyString(), anyString())).thenReturn(DEV_PUBLIC_KEY);
        when(DeviceControllerTestSparkApp.deviceDao.listAllDevices(
          anyString(), anyString())).thenReturn(devices);

        HttpResponse httpResponse = executeListRequest();
        String body = new String(httpResponse.body());

        List<Device> responseDeviceList = GSON.fromJson(body,
          new TypeToken<ArrayList<Device>>(){}.getType());

        assertEquals(devices, responseDeviceList);
        assertEquals(200, httpResponse.code());
        assertNotNull(testServer.getApplication());
        resetMockDaos();
    }

    private HttpResponse executeNewVerifyRequest() throws Exception {
        // Create new device verification request
        DeviceVerificationRequest verificationRequest =
          new DeviceVerificationRequest(NEW_DEV_EMAIL, NEW_DEV_UUID,
            NEW_DEV_VERIFY_CODE);
        return testServer.execute(postMethod(Path.Web.VERIFY,
          signBody(GSON.toJson(verificationRequest))));
    }

    private HttpResponse executeNewDeviceRequest() throws Exception {
        // Create new device verification request
        NewDeviceRequest newDeviceRequest =
          new NewDeviceRequest(NEW_DEV_EMAIL, NEW_DEV_UUID, DEV_PUBLIC_KEY);
        return testServer.execute(postMethod(Path.Web.NEW_DEVICE,
          signBody(GSON.toJson(newDeviceRequest))));
    }

    private HttpResponse executeListRequest() throws Exception {
        // Create new device verification request
        ListDeviceRequest listDeviceRequest =
          new ListDeviceRequest(EXISTING_ACC_UUID, EXISTING_DEV_UUID);
        return testServer.execute(postMethod(Path.Web.LIST_ALL_DEVICES,
          signBody(GSON.toJson(listDeviceRequest))));
    }

    private String signBody(String body) throws Exception {
        // Sign register request
        String bodySigned = PKCUtils.sign(
          DeviceController.SIGNATURE_ALGORITHM,
          PKCUtils.deserializeRSAPrivateKey(DEV_PRIVATE_KEY),
          body.getBytes());
        // Create SignedJSON
        return GSON.toJson(new SignedJSON(body, bodySigned));
    }

    private PostMethod postMethod(String path, String body) {
        return testServer.post(path, body, false);
    }

    private static void resetMockDaos() {
        Mockito.reset(DeviceControllerTestSparkApp.accountDao);
        Mockito.reset(DeviceControllerTestSparkApp.deviceDao);
    }
}