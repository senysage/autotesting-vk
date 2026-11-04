package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class CheckboxesPage {
    private final Page page;
    private final Locator firstCheckbox;

    public CheckboxesPage(Page page) {
        this.page = page;
        firstCheckbox = page.getByRole(AriaRole.CHECKBOX).nth(0);
    }

    public CheckboxesPage open() {
        page.navigate("/checkboxes");
        assertThat(firstCheckbox).isVisible();
        return this;
    }

    public CheckboxesPage checkFirst() {
        firstCheckbox.check();
        return this;
    }

    public CheckboxesPage uncheckFirst() {
        firstCheckbox.uncheck();
        return this;
    }

    public CheckboxesPage assertFirstChecked() {
        assertThat(firstCheckbox).isChecked();
        return this;
    }

    public CheckboxesPage assertFirstUnchecked() {
        assertThat(firstCheckbox).not().isChecked();
        return this;
    }
}
