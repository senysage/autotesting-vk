package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class CheckboxesPage extends LoadablePage<CheckboxesPage> {
    private final Locator checkboxes;

    public CheckboxesPage(Page page) {
        super(page, "/checkboxes", "Checkboxes");
        checkboxes = page.getByRole(AriaRole.CHECKBOX);
    }

    public int checkboxCount() {
        return checkboxes.count();
    }

    public boolean isChecked(int index) {
        return checkboxes.nth(index).isChecked();
    }

    public CheckboxesPage setChecked(int index, boolean checked) {
        checkboxes.nth(index).setChecked(checked);
        return this;
    }

    public CheckboxesPage assertChecked(int index, boolean expectedState) {
        if (expectedState) {
            assertThat(checkboxes.nth(index)).isChecked();
        } else {
            assertThat(checkboxes.nth(index)).not().isChecked();
        }
        return this;
    }

    public CheckboxesPage assertInitialStates() {
        assertAll("Начальные состояния чекбоксов",
                () -> assertThat(checkboxes).hasCount(2),
                () -> assertThat(checkboxes.nth(0)).not().isChecked(),
                () -> assertThat(checkboxes.nth(1)).isChecked());
        return this;
    }

    @Override
    protected void assertContentReady() {
        assertThat(checkboxes.first()).isVisible();
    }

    @Override
    protected CheckboxesPage self() {
        return this;
    }
}
