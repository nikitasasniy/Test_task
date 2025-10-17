package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main {

    private static WebDriver driver;

    //    Запуск браузера
    public static WebDriver startBrowser(String url) {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--headless=new");
            driver = new ChromeDriver(options);
        }
        driver.get(url);
        return driver;
    }

    //     Вставить текст для перевода
    public static void Translate_Text(WebDriverWait wait, String text) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("fakeArea")));
        Actions actions = new Actions(driver);
        actions.click(input).sendKeys(text).perform();
    }

    //    Добавить в избранное
    public static void Add_To_Collection(WebDriverWait wait) {

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#dstBox > div.ilLJxf0PL6bPtN_sUd4r.skRKKGbPIXnTdP4LuRKv.Space_direction_horizontal.Mjf49E6mYwRx8E95h4pg.UEhDFfg0QTWNrnSi1leX.Space_wrap_nowrap.UiHLnQtY16rOS1fNcA_x.dKv0zLmLxIEaMRqFtQOg > div:nth-child(2) > div:nth-child(1) > button")
        ));
        addButton.click();

    }

    public static void open_Fav(WebDriverWait wait) {

        // Прямой клик по вертикальному меню (если развёрнуто)
        try {
            WebElement menuButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#verticalMenuNav > a:nth-child(7)")
            ));
            try {
                // Наведём и кликнем
                Actions actions = new Actions(driver);
                actions.moveToElement(menuButton).perform();
                menuButton.click();
            } catch (Exception clickEx) {
                // fallback: JS click
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuButton);
            }
            return;
        } catch (TimeoutException ignored) {
            System.out.println("Вертикальное меню не доступно или свернуто, пробуем верхнее меню...");
        }

        // Открыть верхнее (header) меню и выбрать пункт
        try {
            WebElement headerMenuBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#headerNavMenuBtn")
            ));
            try {
                Actions actions = new Actions(driver);
                actions.moveToElement(headerMenuBtn).click().perform();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", headerMenuBtn);
            }

            // Ждём и кликаем ссылку "Подборки" в выпадающем меню
            WebElement collectionsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#headerNavMenuPopup > nav > a:nth-child(7)")
            ));
            try {
                new Actions(driver).moveToElement(collectionsLink).click().perform();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", collectionsLink);
            }
            return;
        } catch (TimeoutException ignored) {
            System.out.println("Верхнее меню не сработало...");
        }

        throw new RuntimeException("Не удалось открыть раздел 'Подборки'");
    }


    // Нажатие на кнопку в панели
    public static void click_Panel_Button(WebDriverWait wait) {

        WebElement hoverElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#panel\\:r1\\:0 > div > div.apsVF_0hIliBHK56O81G > div > div > div > div")
        ));
        Actions actions = new Actions(driver);
        actions.moveToElement(hoverElement).perform();

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#panel\\:r1\\:0 > div > div.apsVF_0hIliBHK56O81G > div > div > div > div > div.qV0El6MecS3rADLeQebo > button")
        ));

        button.click();
    }

    // Переместить в подборку
    public static void select_Move_To_Collection(WebDriverWait wait) {
        WebElement moveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("body > div[role='dialog'] button:nth-child(1) > span, body > div[class*='Portal'] button:nth-child(1) > span, div[role='dialog'] button:nth-child(1) > span")

        ));
        moveButton.click();
    }

    // «Новая подборка»
    public static void click_New_Collection(WebDriverWait wait) {
        WebElement newCollectionButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[role='dialog'] div.ixb088pWixFayDy8mtGx div.guk3XWiX3k8jxZvDWM6s > span")
        ));
        newCollectionButton.click();
    }

    // Ввести название новой подборки
    public static void enter_Collection_Name(WebDriverWait wait, String name) {
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[role='dialog'] div.ixb088pWixFayDy8mtGx input[type='text']")
        ));
        nameInput.clear();
        nameInput.sendKeys(name);
    }

    // Деактивировать свитч «Публичная подборка»
    public static void deactivate_Public_Switch(WebDriverWait wait) {
        WebElement switchLabel = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[role='dialog'] div.ixb088pWixFayDy8mtGx label span span")
        ));
        switchLabel.click();
    }

    // Нажать на кнопку «Создать»
    public static void click_Create_Collection(WebDriverWait wait) {
        WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[role='dialog'] div.kjyw4VgIVUyv8d24oIsx button")
        ));
        createButton.click();
    }

    // Закрытие браузера
    public static void quitBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

}
