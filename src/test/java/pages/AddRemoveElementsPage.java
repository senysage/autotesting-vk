package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class AddRemoveElementsPage {
    private final Page page;
    private final Locator addButton;
    private final Locator deleteButtons;

    public AddRemoveElementsPage(Page page) {
        this.page = page;
        addButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Element").setExact(true));
        deleteButtons = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete").setExact(true));
    }

    public AddRemoveElementsPage open() {
        page.navigate("/add_remove_elements/");
        assertThat(addButton).isVisible();
        return this;
    }

    public AddRemoveElementsPage addElement() {
        addButton.click();
        return this;
    }

    public AddRemoveElementsPage removeFirstElement() {
        deleteButtons.first().click();
        return this;
    }

    public AddRemoveElementsPage assertElementCount(int expectedCount) {
        assertThat(deleteButtons).hasCount(expectedCount);
        return this;
    }
}
