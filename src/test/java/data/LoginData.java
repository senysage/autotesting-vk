package data;

import models.Credentials;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

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
                Arguments.of(wrongPassword(), "Your password is invalid!"),
                Arguments.of(new Credentials("unknown_user", VALID_USER.password()), "Your username is invalid!"),
                Arguments.of(new Credentials("", VALID_USER.password()), "Your username is invalid!"),
                Arguments.of(new Credentials(VALID_USER.username(), ""), "Your password is invalid!")
        );
    }
}
