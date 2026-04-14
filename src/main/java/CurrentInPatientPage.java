import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

public class CurrentInPatientPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public CurrentInPatientPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Clicks the second in-patient from the list. If the list is empty or has less than 2 items, navigates back to the home page.
     */
    public void TestInPatient() {
        By itemsLocator = By.xpath(
            "//android.widget.ScrollView//android.view.View[@content-desc]"
        );

        try {
            // Get all items
            List<WebElement> items = driver.findElements(itemsLocator);

            if (items.isEmpty() || items.size() < 2) {
                System.out.println("[INFO] No second in-patient available.");
                System.out.println("Returning to Home page...");
                goBackToHomePage();
                return; // safely exit
            }

            // Click second item (index 1)
            WebElement secondItem = wait.until(
                ExpectedConditions.elementToBeClickable(items.get(1))
            );

            secondItem.click();
            System.out.println("[SUCCESS] Clicked second in-patient");

            // Wait and go back from in-patient details
            Thread.sleep(3000);
            goBackFromInPatientDetails();

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to click in-patient: " + e.getMessage());
        }
    }

    public void goBackToHomePage() {
        By locator;

        if (ios) {
            locator = AppiumBy.accessibilityId("Home");
        } else {
            locator = By.xpath("//android.view.View[contains(@content-desc,'Home')]");
        }

        WebElement homeTab = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        homeTab.click();

        System.out.println("Navigated to Home via bottom nav");
    }

    /**
     * Goes back from in-patient details screen by pressing the back key.
     */
    public void goBackFromInPatientDetails() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }
        System.out.println("Returned from in-patient details");
    }
}