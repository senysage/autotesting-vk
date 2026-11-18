package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public abstract class LoadablePage<T extends LoadablePage<T>> {
    private final Page page;
    private final String path;
    private final Locator heading;

    protected LoadablePage(Page page, String path, String headingText) {
        this.page = page;
        this.path = path;
        heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(headingText).setExact(true));
    }

    public final T get() {
        if (!isLoaded()) {
            load();
        }
        return assertOpened();
    }

    public final T open() {
        load();
        return assertOpened();
    }

    public final T assertOpened() {
        assertAll("Страница " + path,
                () -> assertThat(page).hasURL(path),
                () -> assertThat(heading).isVisible());
        assertContentReady();
        return self();
    }

    protected final Page page() {
        return page;
    }

    protected abstract void assertContentReady();

    protected abstract T self();

    private boolean isLoaded() {
        return page.url().endsWith(path) && heading.isVisible();
    }

    private void load() {
        page.navigate(path);
    }
}
