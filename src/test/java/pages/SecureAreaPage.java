package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import components.FlashMessage;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class SecureAreaPage extends LoadablePage<SecureAreaPage> {
    private final FlashMessage flashMessage;
    private final Locator logoutLink;

    public SecureAreaPage(Page page) {
        super(page, "/secure", "Secure Area");

        flashMessage = new FlashMessage(page.locator("#flash"));
        logoutLink = content().getByRole(AriaRole.LINK,
                new Locator.GetByRoleOptions().setName(Pattern.compile("Logout")));
    }

    public SecureAreaPage assertLoginSuccess() {
        flashMessage.assertSuccess("You logged into a secure area!");
        return this;
    }

    public LoginPage logout() {
        logoutLink.click();
        return new LoginPage(page()).assertOpened();
    }

    @Override
    protected void assertContentReady() {
        assertThat(logoutLink).isVisible();
    }

    @Override
    protected SecureAreaPage self() {
        return this;
    }
}
