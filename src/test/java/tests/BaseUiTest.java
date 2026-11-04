package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import pages.AddRemoveElementsPage;
import pages.CheckboxesPage;
import pages.DropdownPage;
import pages.LoginPage;

abstract class BaseUiTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void startBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty("headless", "true"))));
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(System.getProperty("baseUrl", "https://the-internet.herokuapp.com/"))
                .setViewportSize(1280, 900));
        context.setDefaultTimeout(10_000);
        context.setDefaultNavigationTimeout(30_000);
        page = context.newPage();
    }

    protected LoginPage openLoginPage() {
        return new LoginPage(page).open();
    }

    protected CheckboxesPage openCheckboxesPage() {
        return new CheckboxesPage(page).open();
    }

    protected DropdownPage openDropdownPage() {
        return new DropdownPage(page).open();
    }

    protected AddRemoveElementsPage openAddRemoveElementsPage() {
        return new AddRemoveElementsPage(page).open();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    static void stopBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }
}
