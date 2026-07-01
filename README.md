# Nord Codes API Tests

Black-box API автотесы для Nord Codes приложения.

1) Тестируемое приложение запускается с помощью стандартных средств Java, как процесс OS
2) Мокаем внешний сервис, к которому обращается данное приложение. С использованием WireMock
3) Отправляем запросы через RestAssured
4) Работаем с отчетом и генерируем его, используя инструментарий Allure.

# Test reports / Отчеты о тестировании

You can check the test reports here:
* [Latest Allure Report / Последний Allure-отчёт](https://aitkulovtimur.github.io/nord-codes-api-tests/)
* [CI artifacts: logs, Allure results, Surefire reports / Артефакты CI](https://github.com/AitkulovTimur/nord-codes-api-tests/actions/runs/28507057041)


## Tech stack / Стэк проект

- Java 17
- JUnit 5
- RestAssured
- WireMock
- Allure
- Maven
- GitHub Actions

## Test coverage/Что покрывают тесты

The test suite covers:

- successful and failed `LOGIN`;
- repeated `LOGIN` with the same token;
- `ACTION` without previous `LOGIN`;
- successful `ACTION` after `LOGIN`;
- failed `ACTION` when the external service returns an error;
- `ACTION` after failed `LOGIN`;
- successful `LOGOUT`;
- `ACTION` after `LOGOUT`;
- `LOGOUT` without active session;
- missing and invalid API key;
- invalid token format;
- unknown action type;
- missing required parameters.

## How to run tests / Как запустить тесты

Requirements:

- Java 17
- Maven 3.8+

Run tests:

```bash
mvn clean test
```

---
## How to open Allure report locally/Как открыть Allure отчёт локально
```bash
mvn allure:serve
```
Or generate a static report:
```bash
mvn allure:report
```

## CI/Как работает CI
GitHub Actions workflow runs tests on every push to `main` and publishes the Allure report to GitHub Pages.

## Nuances/нюансы
According to the task description, token should contain `A-Z` and `0-9` characters and have length 32.

During testing it was found that the actual application validates token by other rule:

```text
^[0-9A-F]{32}$
```
