package com.nordcodes.api.tests;

import com.nordcodes.api.BaseApiTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.nordcodes.api.utils.GeneralConstants.ActionType.ACTION;
import static com.nordcodes.api.utils.GeneralConstants.ActionType.LOGIN;
import static com.nordcodes.api.utils.TokenGenerator.validToken;

@Feature("Выполнение действия")
class ActionTests extends BaseApiTest {

    @Test
    @Story("Попытка выполнения действия без входа")
    @DisplayName("Пользователь не может выполнить действие без предварительного входа")
    void actionShouldReturnErrorWhenUserIsNotLoggedIn() {
        String token = validToken();

        Response response = nordCodesApiClient.sendAction(token, ACTION);

        nordCodesAppResponseSteps.verifyError403Response(response);
        externalServiceMock.verifyAuthNotCalled();
        externalServiceMock.verifyDoActionNotCalled();
    }

    @Test
    @Story("Успешное выполнение действия")
    @DisplayName("Пользователь может выполнить действие после успешного входа")
    void actionShouldReturnOkWhenUserSuccessfullyLoggedIn() {
        String token = validToken();

        externalServiceMock.mockAuthSuccess(token);
        externalServiceMock.mockDoActionSuccess(token);

        Response responseLogin = nordCodesApiClient.sendAction(token, LOGIN);
        nordCodesAppResponseSteps.verifySuccessResponse(responseLogin);

        Response responseAction = nordCodesApiClient.sendAction(token, ACTION);
        nordCodesAppResponseSteps.verifySuccessResponse(responseAction);

        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionCalledOnce(token);
    }

    @Test
    @Story("Ошибка внешнего сервиса при выполнении действия")
    @DisplayName("Пользователь получает ошибку, если внешний сервис не смог выполнить действие")
    void actionShouldReturnErrorWhenExternalServiceReturnsError() {
        String token = validToken();

        externalServiceMock.mockAuthSuccess(token);
        externalServiceMock.mockDoActionError(token);

        Response loginResponse = nordCodesApiClient.sendAction(token, LOGIN);
        nordCodesAppResponseSteps.verifySuccessResponse(loginResponse);

        Response actionResponse = nordCodesApiClient.sendAction(token, ACTION);
        nordCodesAppResponseSteps.verifyError500Response(actionResponse);

        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionCalledOnce(token);
    }

    @Test
    @Story("Действие после неуспешного входа")
    @DisplayName("Пользователь не может выполнить действие после неуспешного входа")
    void actionShouldReturnErrorAfterFailedLogin() {
        String token = validToken();

        externalServiceMock.mockAuthError(token);

        Response loginResponse = nordCodesApiClient.sendAction(token, LOGIN);
        nordCodesAppResponseSteps.verifyError500Response(loginResponse);

        Response actionResponse = nordCodesApiClient.sendAction(token, ACTION);
        nordCodesAppResponseSteps.verifyError403Response(actionResponse);

        externalServiceMock.verifyAuthCalledOnce(token);
        externalServiceMock.verifyDoActionNotCalled();
    }

}
