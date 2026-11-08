package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class DropdownPage {
    private final Page page;
    private final Locator dropdown;
    private final Locator selectedOption;

    public DropdownPage(Page page) {
        this.page = page;
        dropdown = page.getByRole(AriaRole.COMBOBOX);
        selectedOption = dropdown.locator("option:checked");
    }

    public DropdownPage open() {
        page.navigate("/dropdown");
        assertThat(dropdown).isVisible();
        return this;
    }

    public DropdownPage selectOption(String value) {
        dropdown.selectOption(value);
        return this;
    }

    public DropdownPage assertSelected(String expectedValue, String expectedText) {
        assertAll("Выбранный пункт списка",
                () -> assertThat(dropdown).hasValue(expectedValue),
                () -> assertThat(selectedOption).hasText(expectedText));
        return this;
    }
}
