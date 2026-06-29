package com.nordcodes.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.nordcodes.api.support.NordCodesAppRunner;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.nordcodes.api.config.ConfigReader.CONFIG;

/**
 * Abstract base class for API tests that provides common setup and teardown functionality.
 * <p>
 * This class manages the lifecycle of the WireMock server and the NordCodes application,
 * ensuring they are properly started before tests and stopped after tests complete.
 * It also configures RestAssured for HTTP testing.
 * </p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {
    protected WireMockServer wireMockServer;
    private NordCodesAppRunner nordCodesAppRunner;

    /**
     * Sets up the test environment before all tests.
     * <p>
     * This method starts the WireMock server, launches the NordCodes application,
     * and configures RestAssured for HTTP testing. If any setup step fails,
     * it performs a complete teardown before re-throwing the exception.
     * </p>
     *
     * @throws RuntimeException if any setup step fails
     */
    @BeforeAll
    protected void globalSetUp() {
        try {
            startWireMock();
            startNordCodesApp();
            configureRestAssured();
        } catch (RuntimeException e) {
            globalTearDown();
            throw e;
        }
    }

    @BeforeEach
    protected void resetWireMock() {
        wireMockServer.resetAll();
    }

    /**
     * Tears down the test environment after all tests.
     * <p>
     * This method stops the NordCodes application and the WireMock server,
     * ensuring proper cleanup of resources.
     * </p>
     */
    @AfterAll
    protected void globalTearDown() {
        stopNordCodesApp();
        stopWireMock();
    }

    protected boolean isNordCodesAppRunning() {
        return nordCodesAppRunner != null && nordCodesAppRunner.isNordCodesAppStarted();
    }

    private void startWireMock() {
        wireMockServer = new WireMockServer(
                options().port(CONFIG.mockPort())
        );
        wireMockServer.start();
    }

    private void startNordCodesApp() {
        nordCodesAppRunner = new NordCodesAppRunner(getClass().getSimpleName());
        nordCodesAppRunner.start();
    }

    private void configureRestAssured() {
        RestAssured.baseURI = CONFIG.nordcodesAppUrl();
    }

    private void stopNordCodesApp() {
        if (nordCodesAppRunner != null) {
            nordCodesAppRunner.stop();
        }
    }

    private void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
