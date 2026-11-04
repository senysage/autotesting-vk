package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Internet: авторизация и элементы интерфейса")
class TheInternetTest extends BaseUiTest {
    // учебные данные со страницы /login
    private static final String USERNAME = "tomsmith";
    private static final String PASSWORD = "SuperSecretPassword!";

    @Test
    @DisplayName("Верные учётные данные открывают защищённую страницу")
    void validUserCanSignIn() {
        openLoginPage()
                .signIn(USERNAME, PASSWORD)
                .assertOpened();
    }

    @Test
    @DisplayName("Неверный пароль вызывает ошибку авторизации")
    void wrongPasswordShowsError() {
        openLoginPage()
                .signInExpectingError(USERNAME, "wrong_password")
                .assertError("Your password is invalid!");
    }

    @Test
    @DisplayName("Первый чекбокс можно установить и снять")
    void checkboxCanBeCheckedAndUnchecked() {
        openCheckboxesPage()
                .assertFirstUnchecked()
                .checkFirst()
                .assertFirstChecked()
                .uncheckFirst()
                .assertFirstUnchecked();
    }

    @Test
    @DisplayName("В выпадающем списке можно выбрать Option 2")
    void dropdownOptionCanBeSelected() {
        openDropdownPage()
                .assertSelected("", "Please select an option")
                .selectOption("2")
                .assertSelected("2", "Option 2");
    }

    @Test
    @DisplayName("Добавленный элемент появляется и затем удаляется")
    void elementCanBeAddedAndRemoved() {
        openAddRemoveElementsPage()
                .assertElementCount(0)
                .addElement()
                .assertElementCount(1)
                .removeFirstElement()
                .assertElementCount(0);
    }
}
