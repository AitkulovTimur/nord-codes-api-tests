package com.nordcodes.api.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.qameta.allure.Step;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.nordcodes.api.utils.GeneralConstants.*;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * Mock steps for the external service that the NordCodes application interacts with.
 * <p>
 * This class provides methods to configure WireMock stubs for external service endpoints
 * and verify that the application makes the expected calls to those endpoints.
 * </p>
 */
public class ExternalServiceMock {
    private static final int CALL_COUNT_ONE = 1;
    private static final int CALL_COUNT_ZERO = 0;
    private final WireMockServer wireMockServer;

    public ExternalServiceMock(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    /**
     * Mocks a successful '/auth' response from the external service.
     *
     * @param token the authentication token to expect in the request
     */
    @Step("Внешний сервис подтверждает вход пользователя")
    public void mockAuthSuccess(String token) {
        mockAuthResponse(token, SC_OK);
    }

    /**
     * Mocks a failed '/auth' response from the external service.
     *
     * @param token the authentication token to expect in the request
     */
    @Step("Внешний сервис отказывает во входе пользователя")
    public void mockAuthError(String token) {
        mockAuthResponse(token, SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Mocks a successful '/doAction' execution response from the external service.
     *
     * @param token the authentication token to expect in the request
     */
    @Step("Внешний сервис успешно выполняет действие")
    public void mockDoActionSuccess(String token) {
        mockDoActionResponse(token, SC_OK);
    }

    /**
     * Mocks a failed '/doAction' execution response from the external service.
     *
     * @param token the authentication token to expect in the request
     */
    @Step("Внешний сервис возвращает ошибку при выполнении действия")
    public void mockDoActionError(String token) {
        mockDoActionResponse(token, SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Verifies that the application called the external '/auth' endpoint exactly once.
     *
     * @param token the authentication token expected in the request
     */
    @Step("Проверяем, что приложение обратилось во внешний сервис для входа")
    public void verifyAuthCalledOnce(String token) {
        wireMockServer.verify(
                CALL_COUNT_ONE,
                postRequestedFor(urlEqualTo(EXTERNAL_AUTH_PATH))
                        .withRequestBody(containing(tokenFormParam(token)))
        );
    }

    /**
     * Verifies that the application called the external '/doAction' endpoint exactly once.
     *
     * @param token the authentication token expected in the request
     */
    @Step("Проверяем, что приложение обратилось во внешний сервис для выполнения действия")
    public void verifyDoActionCalledOnce(String token) {
        wireMockServer.verify(
                CALL_COUNT_ONE,
                postRequestedFor(urlEqualTo(EXTERNAL_DO_ACTION_PATH))
                        .withRequestBody(containing(tokenFormParam(token)))
        );
    }

    /**
     * Verifies that the application did not call the external auth endpoint.
     */
    @Step("Проверяем, что приложение НЕ обратилось во внешний сервис для входа")
    public void verifyAuthNotCalled() {
        wireMockServer.verify(
                CALL_COUNT_ZERO,
                postRequestedFor(urlEqualTo(EXTERNAL_AUTH_PATH)));
    }

    /**
     * Verifies that the application did not call the external '/doAction' endpoint.
     */
    @Step("Проверяем, что приложение НЕ обратилось во внешний сервис для выполнения действия")
    public void verifyDoActionNotCalled() {
        wireMockServer.verify(
                CALL_COUNT_ZERO,
                postRequestedFor(urlEqualTo(EXTERNAL_DO_ACTION_PATH)));
    }

    private void mockAuthResponse(String token, int statusCode) {
        wireMockServer.stubFor(
                post(urlEqualTo(EXTERNAL_AUTH_PATH))
                        .withRequestBody(containing(tokenFormParam(token)))
                        .willReturn(
                                aResponse()
                                        .withStatus(statusCode)
                                        .withHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON))
        );
    }

    private void mockDoActionResponse(String token, int statusCode) {
        wireMockServer.stubFor(
                post(urlEqualTo(EXTERNAL_DO_ACTION_PATH))
                        .withRequestBody(containing(tokenFormParam(token)))
                        .willReturn(
                                aResponse()
                                        .withStatus(statusCode)
                                        .withHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON))
        );
    }

    private String tokenFormParam(String token) {
        return "token=" + token;
    }
}
