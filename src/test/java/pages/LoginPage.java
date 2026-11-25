package pages;

import com.microsoft.playwright.Page;
import components.FlashMessage;
import components.LoginForm;
import models.Credentials;

public final class LoginPage extends LoadablePage<LoginPage> {
    private final LoginForm loginForm;
    private final FlashMessage flashMessage;

    public LoginPage(Page page) {
        super(page, "/login", "Login Page");

        loginForm = new LoginForm(page.locator("#login"));
        flashMessage = new FlashMessage(page.locator("#flash"));
    }

    public SecureAreaPage signIn(Credentials credentials) {
        loginForm.submit(credentials);
        return new SecureAreaPage(page()).assertOpened();
    }

    public LoginPage signInExpectingError(Credentials credentials) {
        loginForm.submit(credentials);
        return assertOpened();
    }

    public LoginPage assertError(String expectedMessage) {
        flashMessage.assertError(expectedMessage);
        return this;
    }

    @Override
    protected void assertContentReady() {
        loginForm.assertReady();
    }

    @Override
    protected LoginPage self() {
        return this;
    }
}
