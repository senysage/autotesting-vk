package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class LoginPage {
    private final Page page;
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator flashMessage;

    public LoginPage(Page page) {
        this.page = page;
        usernameInput = page.getByLabel("Username", new Page.GetByLabelOptions().setExact(true));
        passwordInput = page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true));
        loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("Login")));
        flashMessage = page.locator("#flash");
    }

    public LoginPage open() {
        page.navigate("/login");
        return assertOpened();
    }

    public LoginPage assertOpened() {
        assertAll("Форма входа",
                () -> assertThat(page).hasURL(Pattern.compile("/login$")),
                () -> assertThat(usernameInput).isVisible(),
                () -> assertThat(passwordInput).isVisible(),
                () -> assertThat(loginButton).isVisible());
        return this;
    }

    public SecureAreaPage signIn(String username, String password) {
        submitCredentials(username, password);
        return new SecureAreaPage(page);
    }

    public LoginPage signInExpectingError(String username, String password) {
        submitCredentials(username, password);
        return this;
    }

    public LoginPage assertError(String expectedMessage) {
        assertAll("Ошибка авторизации",
                () -> assertThat(page).hasURL(Pattern.compile("/login$")),
                () -> assertThat(flashMessage).containsText(expectedMessage),
                () -> assertThat(flashMessage).hasClass(Pattern.compile(".*error.*")),
                () -> assertThat(loginButton).isVisible());
        return this;
    }

    private void submitCredentials(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }
}
