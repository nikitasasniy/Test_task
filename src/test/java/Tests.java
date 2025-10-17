import org.example.Main;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

public class Tests {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebElement resultField;


    @BeforeClass
    public void setUp() {
        // Запуск браузера
        driver = Main.startBrowser("https://translate.yandex.ru/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Main.Translate_Text(wait, "こんにちは学生");
        resultField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#dstBox > div.XwtcW4Yp7QVwy8KBxqrs.cn1XJZewVYQhIIcILZmV > div.nI3G8IFy_0MnBmqtxi8Z.udjB9sj0iL22IPftKOQz.e5cUoHm9ElqoZmPrlBug > p > span")));
    }

    //Проверить, что текст перевелся, как «Привет, студент»
    @Test(priority = 1)
    public void test_TranslationIs_Correct() {
        String translatedText = resultField.getText();
        Assert.assertEquals(translatedText, "Привет, студент", "Перевод не совпадает");
    }

    //Проверить, что язык определился, как «Японский»
    @Test(priority = 1)
    public void test_Language_Is_Japanese() {
        WebElement srcLangButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#srcLangButton")));
        String detectedLanguage = srcLangButton.getText();
        Assert.assertEquals(detectedLanguage, "ЯПОНСКИЙ", "Язык определился неверно");
    }

    // проверить отображение подсказки «Голосовой ввод»
    @Test(priority = 1)
    public void test_Voice_Tooltip() {
        WebElement micButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("microphone")));

        Actions actions = new Actions(driver);
        actions.moveToElement(micButton).perform();

        String tooltipText = micButton.getAttribute("data-tooltip");

        Assert.assertNotNull(tooltipText, "Tooltip не найден");
        Assert.assertFalse(tooltipText.isEmpty(), "Tooltip пустой");
    }

    //Проверить, отображение всплывающего сообщения «Перевод добавлен в подборку «Избранное»»
    @Test(priority = 1)
    public void test_Check_Toast() {
        Main.Add_To_Collection(wait);
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#collectionsToast > div")));
            String CheckAdd = toast.getText();
            Assert.assertEquals(CheckAdd, "Перевод добавлен в подборку «Избранное»", CheckAdd);
        } catch (TimeoutException e) {
            Assert.fail("Всплывающее сообщение не появилось");
        }

    }

    //Проверить, что панель «Избранное» активна.
    @Test(priority = 2)
    public void test_Favorites_Tab_Exists() {
        Main.open_Fav(wait);

        try {
            // Ждём появления и видимости заголовка активной вкладки
            WebElement activeTabLabel = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("#panel\\:r1\\:0 > div > div.hTjzPHqbCVNXCUKV2yDc > div.a7BEaDrvxjgG9NgI2FVB > span")
                    )
            );

            String text = activeTabLabel.getText().trim();
            boolean containsFavorites = text.equalsIgnoreCase("Избранное") || text.toLowerCase().contains("избран");

            Assert.assertTrue(containsFavorites, "Текущая вкладка не 'Избранное'. Обнаружен текст: " + text);

            // Тут часто была ошибка при работе в режиме хедлесс, поэтому решил сделать скриншоты в случае ошибки
        } catch (TimeoutException e) {
            takeScreenshot("test_Favorites_Tab_Exists_error.png");
            Assert.fail("Не удалось найти элемент, подтверждающий активность вкладки 'Избранное'");
        } catch (AssertionError e) {
            takeScreenshot("test_Favorites_Tab_Exists_assert.png");
            throw e; // чтобы ошибка всё равно передалась TestNG
        }
    }



    //Проверить, что появилась новая подборка с названием, например «Test» и количество элементов в ней равно «1»
    @Test(priority = 2, dependsOnMethods = {"test_Check_Toast", "test_Favorites_Tab_Exists"})
    public void test_Collection() {
        Main.click_Panel_Button(wait);
        Main.select_Move_To_Collection(wait);
        Main.click_New_Collection(wait);
        Main.enter_Collection_Name(wait, "Test");
        Main.deactivate_Public_Switch(wait);
        Main.click_Create_Collection(wait);

        boolean found = false;
        int attempts = 0;

        while (attempts < 5) {
            try {
                List<WebElement> tabs = wait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[id^='tab\\:r1\\:']"))
                );

                for (WebElement tab : tabs) {
                    List<WebElement> collections = tab.findElements(By.cssSelector("div > div.guk3XWiX3k8jxZvDWM6s"));
                    for (WebElement collection : collections) {

                        WebElement nameElement = collection.findElement(By.cssSelector("span.Text"));
                        String name = nameElement.getText().trim();

                        if (name.equalsIgnoreCase("Test")) {
                            found = true;
                            WebElement countElement = collection.findElement(By.cssSelector(
                                    "span.Text.NwslJhSesN1suNpBe4hy.wys8db2rsYt2l5vK9OFU.XIDYGWsoTGAEBIcv3FkQ.YrEYeqPS_UPLU4JgRSnt.pRLjXL4DRFq0fALmDxJb.GZHO8xV8rEfuWzso2N04.SNV4Fi0YUQbaCGPS7AZL.U9OqKta6JYsHoyDH7IKL > span"
                            ));

                            int actualCount = Integer.parseInt(countElement.getText().trim());
                            Assert.assertEquals(actualCount, 1, "Количество элементов в коллекции 'Test' неверное");
                            return;
                        }
                    }
                }

                if (found) break;

            } catch (Exception e) {
                System.out.println("Попытка " + (attempts + 1) + " не удалась: " + e.getMessage());
            }

            attempts++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        Assert.assertTrue(found, "Коллекция 'Test' не найдена после 5 попыток");
    }

    @AfterClass
    public void tearDown() {
        Main.quitBrowser();
    }

    @AfterMethod
    public void captureOnFailure(ITestResult result) {
        if (!result.isSuccess()) {
            takeScreenshot(result.getName() + ".png");
        }
    }

    // Метод для снятия скриншота
    private void takeScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File("screenshots/" + fileName);
            destination.getParentFile().mkdirs();
            Files.copy(screenshot.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Скриншот сохранён: " + destination.getAbsolutePath());
        } catch (Exception ex) {
            System.err.println("Не удалось сохранить скриншот: " + ex.getMessage());
        }
    }
}
