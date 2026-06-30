package com.nordcodes.api.tests;

import com.nordcodes.api.BaseApiTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.nordcodes.api.utils.GeneralConstants.ActionType.LOGIN;
import static com.nordcodes.api.utils.TokenGenerator.validToken;

@Feature("Вход пользователя")
class LoginTests extends BaseApiTest {

    @Test
    @Story("Успешный вход")
    @DisplayName("Пользователь успешно входит, если внешний сервис подтвердил токен")
    void loginShouldReturnOkWhenExternalServiceConfirmedLogin() {
        String token = validToken();

        externalServiceMock.mockAuthSuccess(token);

        Response response = nordCodesApiClient.sendAction(token, LOGIN);

        nordCodesAppResponseSteps.verifySuccessResponse(response);
        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Test
    @Story("Ошибка входа")
    @DisplayName("Пользователь не входит, если внешний сервис отклонил токен")
    void loginShouldReturnErrorWhenExternalServiceRejectedLogin() {
        String token = validToken();

        externalServiceMock.mockAuthError(token);

        Response response = nordCodesApiClient.sendAction(token, LOGIN);

        nordCodesAppResponseSteps.verifyError500Response(response);
        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Test
    @Story("Ошибка входа с токеном переданным ранее")
    @DisplayName("Пользователь не входит, если пытается передать токен, который уже существует в системе")
    void secondLoginShouldReturnConflictWhenTokenAlreadyExists() {
        String token = validToken();

        externalServiceMock.mockAuthSuccess(token);

        Response response = nordCodesApiClient.sendAction(token, LOGIN);
        nordCodesAppResponseSteps.verifySuccessResponse(response);

        Response secondResponse = nordCodesApiClient.sendAction(token, LOGIN);
        nordCodesAppResponseSteps.verifyError409Response(secondResponse);

        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionNotCalled();
    }


}
