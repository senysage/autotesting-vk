package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pages.LoginPage;

@Tag("authentication")
@DisplayName("The Internet: авторизация")
class LoginTest extends BaseUiTest {
    private static final String USERNAME = "tomsmith";
    private static final String PASSWORD = "SuperSecretPassword!";
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
            loginPage.signIn(USERNAME, PASSWORD)
                    .assertOpened();
        }

        @Test
        @DisplayName("После выхода снова отображается форма входа")
        void userCanLogOut() {
            loginPage.signIn(USERNAME, PASSWORD)
                    .assertOpened()
                    .logout()
                    .assertOpened();
        }
    }

    @Nested
    @DisplayName("Неверные учётные данные")
    class InvalidCredentials {
        @ParameterizedTest(name = "Набор {index}: {2}")
        @CsvSource({
                "tomsmith, wrong_password, Your password is invalid!",
                "unknown_user, SuperSecretPassword!, Your username is invalid!",
                "'', SuperSecretPassword!, Your username is invalid!",
                "tomsmith, '', Your password is invalid!"
        })
        @DisplayName("Ошибка авторизации: неверные данные")
        void invalidCredentialsShowError(String username, String password, String expectedError) {
            loginPage.signInExpectingError(username, password)
                    .assertError(expectedError);
        }
    }
}
