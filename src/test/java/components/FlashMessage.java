package components;

import com.microsoft.playwright.Locator;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class FlashMessage {
    private final Locator root;

    public FlashMessage(Locator root) {
        this.root = root;
    }

    public FlashMessage assertError(String expectedText) {
        return assertMessage(expectedText, "error");
    }

    public FlashMessage assertSuccess(String expectedText) {
        return assertMessage(expectedText, "success");
    }

    private FlashMessage assertMessage(String expectedText, String status) {
        assertAll("Уведомление",
                () -> assertThat(root).isVisible(),
                () -> assertThat(root).containsText(expectedText),
                () -> assertThat(root).hasClass(Pattern.compile(".*\\b" + status + "\\b.*")));
        return this;
    }
}
