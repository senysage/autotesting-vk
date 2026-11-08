package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@Tag("elements")
@DisplayName("The Internet: элементы интерфейса")
class TheInternetTest extends BaseUiTest {
    @Test
    @Tag("smoke")
    @DisplayName("Начальные состояния чекбоксов соответствуют ожидаемым")
    void checkboxesHaveExpectedInitialStates() {
        openCheckboxesPage()
                .assertInitialStates();
    }

    @Test
    @Disabled("Заменён динамическими проверками всех чекбоксов")
    @DisplayName("Первый чекбокс можно установить и снять")
    void checkboxCanBeCheckedAndUnchecked() {
        openCheckboxesPage()
                .assertChecked(0, false)
                .setChecked(0, true)
                .assertChecked(0, true)
                .setChecked(0, false)
                .assertChecked(0, false);
    }

    @ParameterizedTest(name = "Выбор {1}, значение {0}")
    @CsvSource({"1, Option 1", "2, Option 2"})
    @DisplayName("В выпадающем списке можно выбрать каждый доступный пункт")
    void dropdownOptionCanBeSelected(String value, String text) {
        openDropdownPage()
                .assertSelected("", "Please select an option")
                .selectOption(value)
                .assertSelected(value, text);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Добавленный элемент появляется и затем удаляется")
    void elementCanBeAddedAndRemoved() {
        openAddRemoveElementsPage()
                .assertElementCount(0)
                .addElement()
                .assertElementCount(1)
                .removeFirstElement()
                .assertElementCount(0);
    }

    @TestFactory
    @Tag("dynamic")
    @DisplayName("Каждый чекбокс на странице можно переключить и вернуть в исходное состояние")
    Stream<DynamicTest> everyCheckboxCanBeToggledAndRestored() {
        int checkboxCount = openCheckboxesPage().checkboxCount();
        assertTrue(checkboxCount > 0, "На странице должны быть чекбоксы");

        return IntStream.range(0, checkboxCount).mapToObj(index -> dynamicTest(
                "Переключение чекбокса №" + (index + 1),
                () -> assertTimeout(Duration.ofSeconds(45), () ->
                    withCheckboxesPage(checkboxesPage -> {
                        boolean initialState = checkboxesPage.isChecked(index);
                        checkboxesPage.setChecked(index, !initialState)
                                .assertChecked(index, !initialState)
                                .setChecked(index, initialState)
                                .assertChecked(index, initialState);
                    }))));
    }
}
