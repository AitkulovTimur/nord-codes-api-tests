package com.nordcodes.api.tests;

import com.nordcodes.api.BaseApiTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.nordcodes.api.utils.GeneralConstants.ActionType.*;
import static com.nordcodes.api.utils.TokenGenerator.validToken;

@Feature("Жизненный цикл сессии")
class SessionLifecycleTests extends BaseApiTest {
    private static final String EXPECTED_MESSAGE_PART = "not found";

    @Test
    @Story("Выход после входа")
    @DisplayName("Пользователь может выйти после успешного входа")
    void logoutShouldReturnOkAfterSuccessfulLogin() {
        String token = validToken();

        externalServiceMock.mockAuthSuccess(token);

        Response loginResponse = nordCodesApiClient.sendAction(token, LOGIN);
        nordCodesAppResponseSteps.verifySuccessResponse(loginResponse);

        Response logoutResponse = nordCodesApiClient.sendAction(token, LOGOUT);
        nordCodesAppResponseSteps.verifySuccessResponse(logoutResponse);

        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Test
    @Story("После выхода из учетной записи доступное ранее действие становится недоступным")
    @DisplayName("После выхода пользователь больше не может выполнять защищённые действия")
    void actionShouldBecomeUnavailableAfterLogout() {
        String token = validToken();

        externalServiceMock.mockAuthSuccess(token);
        externalServiceMock.mockDoActionSuccess(token);

        Response loginResponse = nordCodesApiClient.sendAction(token, LOGIN);
        nordCodesAppResponseSteps.verifySuccessResponse(loginResponse);

        Response actionResponse = nordCodesApiClient.sendAction(token, ACTION);
        nordCodesAppResponseSteps.verifySuccessResponse(actionResponse);

        Response logoutResponse = nordCodesApiClient.sendAction(token, LOGOUT);
        nordCodesAppResponseSteps.verifySuccessResponse(logoutResponse);

        Response actionErrorResponse = nordCodesApiClient.sendAction(token, ACTION);
        nordCodesAppResponseSteps.verifyError403Response(actionErrorResponse, EXPECTED_MESSAGE_PART);

        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionCalledOnce(token);
    }

    @Test
    @Story("Выход без активной сессии")
    @DisplayName("Пользователь не может выйти, если активной сессии нет")
    void logoutShouldReturnErrorWhenUserIsNotLoggedIn() {
        String token = validToken();

        Response logoutResponse = nordCodesApiClient.sendAction(token, LOGOUT);
        nordCodesAppResponseSteps.verifyError403Response(logoutResponse);

        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }
}
