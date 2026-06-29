package com.nordcodes.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static com.nordcodes.api.config.ConfigReader.CONFIG;
import static com.nordcodes.api.utils.GeneralConstants.*;
import static io.restassured.RestAssured.given;

/**
 * HTTP client for interacting with the NordCodes application API.
 * <p>
 * This class provides methods to send actions to the NordCodes application endpoint
 * with various authentication configurations.
 * </p>
 */
public class NordCodesApiClient {
    /**
     * Sends an action to the NordCodes application with the configured API key.
     *
     * @param token the authentication token
     * @param action the action to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {action}")
    public Response sendAction(String token, String action) {
        return given()
                .contentType(CONTENT_TYPE_FORM)
                .accept(CONTENT_TYPE_JSON)
                .header(API_KEY_HEADER_NAME, CONFIG.apiKey())
                .formParam(TOKEN_FIELD_NAME, token)
                .formParam(ACTION_FIELD_NAME, action)
                .when()
                .post(ENDPOINT_PATH);
    }

    /**
     * Sends an action to the NordCodes application without an API key.
     * <p>
     * This method is used to test authentication failure scenarios.
     * </p>
     *
     * @param token the authentication token
     * @param action the action to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {action}, не используя API-ключ")
    public Response sendActionWithoutApiKey(String token, String action) {
        return given()
                .contentType(CONTENT_TYPE_FORM)
                .accept(CONTENT_TYPE_JSON)
                .formParam(TOKEN_FIELD_NAME, token)
                .formParam(ACTION_FIELD_NAME, action)
                .when()
                .post(ENDPOINT_PATH);
    }

    /**
     * Sends an action to the NordCodes application with a custom API key.
     * <p>
     * This method is used to test scenarios with custom or invalid API keys.
     * </p>
     *
     * @param apiKey the custom API key to use
     * @param token the authentication token
     * @param action the action to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {action}, используя переданный ключ доступа")
    public Response sendActionWithApiKey(String apiKey, String token, String action) {
        return given()
                .contentType(CONTENT_TYPE_FORM)
                .accept(CONTENT_TYPE_JSON)
                .header(API_KEY_HEADER_NAME, apiKey)
                .formParam(TOKEN_FIELD_NAME, token)
                .formParam(ACTION_FIELD_NAME, action)
                .when()
                .post(ENDPOINT_PATH);
    }

}
