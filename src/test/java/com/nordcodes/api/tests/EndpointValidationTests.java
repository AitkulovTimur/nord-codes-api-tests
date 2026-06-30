package com.nordcodes.api.tests;

import com.nordcodes.api.BaseApiTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.nordcodes.api.utils.GeneralConstants.ActionType;
import static com.nordcodes.api.utils.GeneralConstants.ActionType.ACTION;
import static com.nordcodes.api.utils.GeneralConstants.ActionType.LOGIN;
import static com.nordcodes.api.utils.GeneralConstants.ActionType.LOGOUT;
import static com.nordcodes.api.utils.TokenGenerator.validToken;

@Feature("Проверка входящих запросов")
class EndpointValidationTests extends BaseApiTest {

    private static final String UNKNOWN_ACTION = "UNKNOWN_ACTION";
    private static final String INVALID_TOKEN = "INVALID_TOKEN";
    private static final String INVALID_API_KEY = "invalid-api-key";
    private static final String EXPECTED_API_KEY_MESSAGE = "Missing or invalid API Key";

    private static Stream<Arguments> actionTypes() {
        return Stream.of(
                Arguments.of(LOGIN, "вход пользователя"),
                Arguments.of(ACTION, "выполнение действия"),
                Arguments.of(LOGOUT, "выход пользователя")
        );
    }

    @Story("Проверка ключа доступа")
    @DisplayName("Проверка запросов без ключа доступа")
    @ParameterizedTest(name = "{index}. Запрос на {1} отклоняется, если ключ доступа не передан")
    @MethodSource("actionTypes")
    void requestShouldReturnErrorWhenApiKeyIsMissing(ActionType actionType, String actionName) {
        String token = validToken();

        Response response = nordCodesApiClient.sendActionWithoutApiKey(token, actionType);

        nordCodesAppResponseSteps.verifyError401Response(response, EXPECTED_API_KEY_MESSAGE);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Story("Проверка ключа доступа")
    @DisplayName("Проверка запросов с неверным ключом доступа")
    @ParameterizedTest(name = "{index}. Запрос на {1} отклоняется, если ключ доступа неверный")
    @MethodSource("actionTypes")
    void requestShouldReturnErrorWhenApiKeyIsInvalid(ActionType actionType, String actionName) {
        String token = validToken();

        Response response = nordCodesApiClient.sendActionWithApiKey(INVALID_API_KEY, token, actionType);

        nordCodesAppResponseSteps.verifyError401Response(response, EXPECTED_API_KEY_MESSAGE);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Story("Проверка формата токена для разных действий")
    @DisplayName("Проверка невалидного токена для разных действий")
    @ParameterizedTest(name = "{index}. Запрос на {1} отклоняется, если токен имеет неверный формат")
    @MethodSource("actionTypes")
    void requestShouldReturnErrorWhenTokenIsInvalidForDifferentActions(ActionType actionType, String actionName) {
        Response response = nordCodesApiClient.sendAction(INVALID_TOKEN, actionType);

        nordCodesAppResponseSteps.verifyError400Response(response);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Story("Валидация формата токена")
    @DisplayName("Проверка разных вариантов невалидного токена")
    @ParameterizedTest(name = "{index}. Запрос отклоняется, если токен невалиден: {1}")
    @CsvSource(value = {
            "ABCDEF0123456789ABCDEF012345678 | слишком короткий: 31 символ",
            "ABCDEF0123456789ABCDEF01234567890 | слишком длинный: 33 символа",
            "abcdef0123456789abcdef0123456789 | содержит строчные буквы",
            "GHIJKL0123456789GHIJKL0123456789 | содержит символы вне допустимого диапазона",
            "ABCDEF0123456789ABCDEF01234567!! | содержит спецсимволы"
    }, delimiter = '|')
    void requestShouldReturnErrorWhenTokenHasInvalidFormat(String invalidToken, String reason) {
        Response response = nordCodesApiClient.sendAction(invalidToken, LOGIN);

        nordCodesAppResponseSteps.verifyError400Response(response);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Test
    @Story("Проверка типа действия")
    @DisplayName("Запрос отклоняется, если передан неизвестный тип действия")
    void requestShouldReturnErrorWhenActionIsUnknown() {
        String token = validToken();

        Response response = nordCodesApiClient.sendAction(token, UNKNOWN_ACTION);

        nordCodesAppResponseSteps.verifyError400Response(response);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Test
    @Story("Проверка обязательных параметров")
    @DisplayName("Запрос отклоняется, если не передан токен")
    void requestShouldReturnErrorWhenTokenIsMissing() {
        Response response = nordCodesApiClient.sendActionWithoutToken(LOGIN);

        nordCodesAppResponseSteps.verifyError400Response(response);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Test
    @Story("Проверка обязательных параметров")
    @DisplayName("Запрос отклоняется, если не передан тип действия")
    void requestShouldReturnErrorWhenActionIsMissing() {
        String token = validToken();

        Response response = nordCodesApiClient.sendRequestWithoutAction(token);

        nordCodesAppResponseSteps.verifyError400Response(response);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }
}
