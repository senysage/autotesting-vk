package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import models.Credentials;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class LoginForm {
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator submitButton;

    public LoginForm(Locator root) {
        usernameInput = root.getByLabel("Username", new Locator.GetByLabelOptions().setExact(true));
        passwordInput = root.getByLabel("Password", new Locator.GetByLabelOptions().setExact(true));
        submitButton = root.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName(Pattern.compile("Login")));
    }

    public LoginForm assertReady() {
        assertAll("Форма входа",
                () -> assertThat(usernameInput).isVisible(),
                () -> assertThat(passwordInput).isVisible(),
                () -> assertThat(submitButton).isVisible());
        return this;
    }

    public LoginForm submit(Credentials credentials) {
        usernameInput.fill(credentials.username());
        passwordInput.fill(credentials.password());
        submitButton.click();
        return this;
    }
}
