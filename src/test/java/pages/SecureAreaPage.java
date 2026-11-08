package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class SecureAreaPage {
    private final Page page;
    private final Locator heading;
    private final Locator flashMessage;
    private final Locator logoutLink;

    public SecureAreaPage(Page page) {
        this.page = page;
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Secure Area").setExact(true));
        flashMessage = page.locator("#flash");
        logoutLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("Logout")));
    }

    public SecureAreaPage assertOpened() {
        assertAll("Защищённая страница",
                () -> assertThat(page).hasURL(Pattern.compile("/secure$")),
                () -> assertThat(heading).isVisible(),
                () -> assertThat(flashMessage).containsText("You logged into a secure area!"),
                () -> assertThat(logoutLink).isVisible());
        return this;
    }

    public LoginPage logout() {
        logoutLink.click();
        return new LoginPage(page);
    }
}
