package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import models.Credentials;
import pages.LoginPage;

import static data.LoginData.validUser;
import static data.LoginData.wrongPassword;

@Tag("authentication")
@DisplayName("The Internet: авторизация")
class LoginTest extends BaseUiTest {
    private LoginPage loginPage;

    @BeforeEach
    void openLoginForm() {
        loginPage = openLoginPage();
    }

    @Nested
    @DisplayName("Верные учётные данные")
    class ValidCredentials {
        @Test
        @Tag("smoke")
        @DisplayName("Пользователь попадает в защищённую область")
        void validUserCanSignIn() {
            loginPage.signIn(validUser())
                    .assertLoginSuccess();
        }

        @Test
        @DisplayName("После выхода снова отображается форма входа")
        void userCanLogOut() {
            loginPage.signIn(validUser())
                    .assertLoginSuccess()
                    .logout()
                    .assertOpened();
        }

        @Test
        @DisplayName("Повторное открытие защищённой страницы сохраняет авторизацию")
        void sessionSurvivesReopeningSecureArea() {
            loginPage.signIn(validUser())
                    .get()
                    .assertLoginSuccess()
                    .open()
                    .assertOpened();
        }
    }

    @Nested
    @DisplayName("Неверные учётные данные")
    class InvalidCredentials {
        @ParameterizedTest(name = "{0}")
        @MethodSource("data.LoginData#invalidCredentials")
        @DisplayName("Ошибка авторизации: неверные данные")
        void invalidCredentialsShowError(Credentials credentials, String expectedError) {
            loginPage.signInExpectingError(credentials)
                    .assertError(expectedError);
        }

        @Test
        @DisplayName("После ошибки можно войти с правильными учётными данными")
        void userCanSignInAfterCorrectingPassword() {
            loginPage.signInExpectingError(wrongPassword())
                    .assertError("Your password is invalid!")
                    .signIn(validUser())
                    .assertLoginSuccess();
        }
    }
}
