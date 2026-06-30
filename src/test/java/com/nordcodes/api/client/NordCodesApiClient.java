package com.nordcodes.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static com.nordcodes.api.specs.Specifications.nordCodesRequestSpec;
import static com.nordcodes.api.specs.Specifications.nordCodesRequestSpecWithApiKey;
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
     * @param token  the authentication token
     * @param actionType the action of type {@link ActionType} to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {actionType}")
    public Response sendAction(String token, ActionType actionType) {
        return given()
                .spec(nordCodesRequestSpecWithApiKey())
                .formParam(TOKEN_FIELD_NAME, token)
                .formParam(ACTION_FIELD_NAME, actionType.name())
                .when()
                .post(ENDPOINT_PATH);
    }

    /**
     * Sends an action to the NordCodes application with the configured API key.
     *
     * @param token  the authentication token
     * @param actionType the action of type {@link String} to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {actionType}")
    public Response sendAction(String token, String actionType) {
        return given()
                .spec(nordCodesRequestSpecWithApiKey())
                .formParam(TOKEN_FIELD_NAME, token)
                .formParam(ACTION_FIELD_NAME, actionType)
                .when()
                .post(ENDPOINT_PATH);
    }

    /**
     * Sends an action to the NordCodes application without the token form parameter.
     *
     * @param actionType the action of type {@link ActionType}  to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {actionType} без token")
    public Response sendActionWithoutToken(ActionType actionType) {
        return given()
                .spec(nordCodesRequestSpecWithApiKey())
                .formParam(ACTION_FIELD_NAME, actionType.name())
                .when()
                .post(ENDPOINT_PATH);
    }

    /**
     * Sends a request to the NordCodes application without the action form parameter.
     *
     * @param token the authentication token
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение запрос без action")
    public Response sendRequestWithoutAction(String token) {
        return given()
                .spec(nordCodesRequestSpecWithApiKey())
                .formParam(TOKEN_FIELD_NAME, token)
                .when()
                .post(ENDPOINT_PATH);
    }

    /**
     * Sends an action to the NordCodes application without an API key.
     * <p>
     * This method is used to test authentication failure scenarios.
     * </p>
     *
     * @param token  the authentication token
     * @param actionType the action of type {@link ActionType} to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {actionType}, не используя API-ключ")
    public Response sendActionWithoutApiKey(String token, ActionType actionType) {
        return given()
                .spec(nordCodesRequestSpec())
                .formParam(TOKEN_FIELD_NAME, token)
                .formParam(ACTION_FIELD_NAME, actionType.name())
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
     * @param token  the authentication token
     * @param actionType the action of type {@link ActionType} to perform
     * @return the HTTP response from the application
     */
    @Step("Отправляем в приложение действие {actionType}, используя переданный ключ доступа")
    public Response sendActionWithApiKey(String apiKey, String token, ActionType actionType) {
        return given()
                .spec(nordCodesRequestSpec())
                .header(API_KEY_HEADER_NAME, apiKey)
                .formParam(TOKEN_FIELD_NAME, token)
                .formParam(ACTION_FIELD_NAME, actionType.name())
                .when()
                .post(ENDPOINT_PATH);
    }

}
