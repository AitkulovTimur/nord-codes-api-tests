package com.nordcodes.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.Matcher;

import static com.nordcodes.api.specs.Specifications.nordCodesResponseSpec;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static com.nordcodes.api.utils.GeneralConstants.*;

/**
 * Assertion steps for validating NordCodes application responses.
 */
public class NordCodesAppResponseSteps {

    private static final Matcher<String> ERROR_FIELD_MATCHER = equalTo("ERROR");
    private static final Matcher<String> OK_FIELD_MATCHER = equalTo("OK");

    /**
     * Verifies a successful NordCodes API response.
     *
     * @param response response returned by the NordCodes application
     */
    @Step("Проверяем, что приложение вернуло успешный результат")
    public void verifySuccessResponse(Response response) {
        response.then()
                .spec(nordCodesResponseSpec())
                .statusCode(SC_OK)
                .body(RESULT_FIELD_NAME, OK_FIELD_MATCHER);
    }

    /**
     * Verifies an error response with HTTP 400 status.
     *
     * @param response response returned by the NordCodes application
     */
    public void verifyError400Response(Response response) {
        verifyErrorResponse(response, SC_BAD_REQUEST);
    }

    /**
     * Verifies an error response with HTTP 401 status and expected message fragment.
     *
     * @param response            response returned by the NordCodes application
     * @param expectedMessagePart expected fragment of the error message
     */
    public void verifyError401Response(Response response, String expectedMessagePart) {
        verifyErrorResponse(response, SC_UNAUTHORIZED, expectedMessagePart);
    }

    /**
     * Verifies an error response with HTTP 403 status.
     *
     * @param response response returned by the NordCodes application
     */
    public void verifyError403Response(Response response) {
        verifyErrorResponse(response, SC_FORBIDDEN);
    }

    /**
     * Verifies an error response with HTTP 403 status and expected message fragment.
     *
     * @param response            response returned by the NordCodes application
     * @param expectedMessagePart expected fragment of the error message
     */
    public void verifyError403Response(Response response, String expectedMessagePart) {
        verifyErrorResponse(response, SC_FORBIDDEN, expectedMessagePart);
    }

    /**
     * Verifies an error response with HTTP 409 status.
     *
     * @param response response returned by the NordCodes application
     */
    public void verifyError409Response(Response response) {
        verifyErrorResponse(response, SC_CONFLICT);
    }

    /**
     * Verifies an error response with HTTP 500 status.
     *
     * @param response response returned by the NordCodes application
     */
    public void verifyError500Response(Response response) {
        verifyErrorResponse(response, SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Verifies the common error response shape for the expected HTTP status.
     *
     * @param response           response returned by the NordCodes application
     * @param expectedStatusCode expected HTTP status code
     */
    @Step("Проверяем, что приложение вернуло ошибку с HTTP-кодом {expectedStatusCode}")
    public void verifyErrorResponse(Response response, int expectedStatusCode) {
        response.then()
                .spec(nordCodesResponseSpec())
                .statusCode(expectedStatusCode)
                .body(RESULT_FIELD_NAME, ERROR_FIELD_MATCHER)
                .body(MESSAGE_FIELD_NAME, not(emptyOrNullString()));
    }

    /**
     * Verifies the common error response shape and expected message fragment.
     *
     * @param response            response returned by the NordCodes application
     * @param expectedStatusCode  expected HTTP status code
     * @param expectedMessagePart expected fragment of the error message
     */
    @Step("Проверяем, что приложение вернуло ошибку с HTTP-кодом {expectedStatusCode} и сообщением, содержащим {expectedMessagePart}")
    public void verifyErrorResponse(Response response, int expectedStatusCode, String expectedMessagePart) {
        response.then()
                .spec(nordCodesResponseSpec())
                .statusCode(expectedStatusCode)
                .body(RESULT_FIELD_NAME, ERROR_FIELD_MATCHER)
                .body(MESSAGE_FIELD_NAME, not(emptyOrNullString()))
                .body(MESSAGE_FIELD_NAME, containsString(expectedMessagePart));
    }

}
