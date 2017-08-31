package com.majoinen.d.pwcrypt.tracker.device;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Majoinen
 * @version 1.0, 31/8/17
 */
public class DeviceTest {

    private static final String UUID = "UUID";

    private static final String IP = "192.168.0.1";

    private static final String PLATFORM = "Desktop";

    private static final String PUBLIC_KEY = "pubkey";

    private Device device;

    @Before
    public void beforeEachTest() throws Exception {
        device = new Device(null, null, null, null);
    }

    @Test
    public void setUuid() throws Exception {
        device.setUuid(UUID);
        assertTrue(device.getUuid().equals(UUID));
    }

    @Test
    public void setIp() throws Exception {
        device.setIp(IP);
        assertTrue(device.getIp().equals(IP));
    }

    @Test
    public void setPlatform() throws Exception {
        device.setPlatform(PLATFORM);
        assertTrue(device.getPlatform().equals(PLATFORM));
    }

    @Test
    public void setPublicKey() throws Exception {
        device.setPublicKey(PUBLIC_KEY);
        assertTrue(device.getPublicKey().equals(PUBLIC_KEY));
    }

    @Test
    public void equals() throws Exception {
        assertEquals(device, device);
    }

    @Test
    public void equalsNotInstanceOf() throws Exception {
        assertFalse(device.equals(new String("DEVICE")));
    }

    @Test
    public void hashCodeEqual() throws Exception {
        Device device1 = new Device(UUID, IP, PLATFORM, PUBLIC_KEY);
        Device device2 = new Device(UUID, IP, PLATFORM, PUBLIC_KEY);
        assertEquals(device1.hashCode(), device2.hashCode());
    }
}