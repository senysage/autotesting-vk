package data;

import models.Credentials;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;

public final class LoginData {
    private static final Credentials VALID_USER = new Credentials("tomsmith", "SuperSecretPassword!");

    private LoginData() {
    }

    public static Credentials validUser() {
        return VALID_USER;
    }

    public static Credentials wrongPassword() {
        return new Credentials(VALID_USER.username(), "wrong_password");
    }

    public static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of(named("Неверный пароль", wrongPassword()), "Your password is invalid!"),
                Arguments.of(named("Неизвестный пользователь", new Credentials("unknown_user", VALID_USER.password())),
                        "Your username is invalid!"),
                Arguments.of(named("Пустое имя пользователя", new Credentials("", VALID_USER.password())),
                        "Your username is invalid!"),
                Arguments.of(named("Пустой пароль", new Credentials(VALID_USER.username(), "")),
                        "Your password is invalid!")
        );
    }
}
