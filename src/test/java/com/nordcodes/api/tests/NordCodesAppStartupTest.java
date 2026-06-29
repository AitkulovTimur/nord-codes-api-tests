package com.nordcodes.api.tests;

import com.nordcodes.api.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NordCodesAppStartupTest extends BaseApiTest {
    @Test
    @DisplayName("NordCodes app and WireMock server start successfully")
    void testAppStartup() {
        assertAll(
                () -> assertTrue(wireMockServer.isRunning(), "WireMock server should be running"),
                () -> assertTrue(isNordCodesAppRunning(), "NordCodes app should be running")
        );
    }
}
