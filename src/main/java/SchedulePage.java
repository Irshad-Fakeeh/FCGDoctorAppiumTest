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

public class SchedulePage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public SchedulePage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    public void testSchedulePage() throws InterruptedException {

        System.out.println("Switching from Physical to Virtual tab...");
        switchFromPhysicalToVirtual();
        Thread.sleep(2000);

        System.out.println("Switching from Virtual to Physical tab...");
        switchFromVirtualToPhysical();
        Thread.sleep(2000);

        System.out.println("Waiting for first appointment selection...");
        clickFirstAppointment();
        Thread.sleep(3000);

        System.out.println("Going back from appointment details...");
        goBackFromAppointmentDetails();
        Thread.sleep(2000);
    }
    /**
     * Clicks the Physical appointments tab.
     */
    public void clickPhysicalTab() {
        By locator = ios
                ? AppiumBy.accessibilityId("Physical")
                : By.xpath("//*[contains(@content-desc,'Physical') or contains(@text,'Physical')]");

        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(locator));
        tab.click();
        System.out.println("Switched to Physical tab.");
    }

    /**
     * Clicks the Virtual appointments tab.
     */
    public void clickVirtualTab() {
        By locator = ios
                ? AppiumBy.accessibilityId("Virtual")
                : By.xpath("//*[contains(@content-desc,'Virtual') or contains(@text,'Virtual')]");

        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(locator));
        tab.click();
        System.out.println("Switched to Virtual tab.");
    }

    /**
     * Switches from the Physical tab to the Virtual tab.
     */
    public void switchFromPhysicalToVirtual() {
        clickVirtualTab();
    }

    /**
     * Switches from the Virtual tab to the Physical tab.
     */
    public void switchFromVirtualToPhysical() {
        clickPhysicalTab();
    }

    /**
     * Verifies the Physical tab is displayed.
     */
    public boolean isPhysicalTabVisible() {
        By locator = ios
                ? AppiumBy.accessibilityId("Physical")
                : By.xpath("//*[contains(@content-desc,'Physical') or contains(@text,'Physical')]");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies the Virtual tab is displayed.
     */
    public boolean isVirtualTabVisible() {
        By locator = ios
                ? AppiumBy.accessibilityId("Virtual")
                : By.xpath("//*[contains(@content-desc,'Virtual') or contains(@text,'Virtual')]");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickFirstAppointment() {

    By itemsLocator = By.xpath(
        "//android.widget.ScrollView//android.view.View[@content-desc]"
    );

    try {
        // Get all items
        List<WebElement> items = driver.findElements(itemsLocator);

        if (items.isEmpty()) {
            System.out.println("[INFO] No appointments available.");
            System.out.println("Returning to Home page...");
            goBackToHomePage();
            return; // safely exit
        }

        // Click first item
        WebElement firstItem = wait.until(
            ExpectedConditions.elementToBeClickable(items.get(0))
        );

        firstItem.click();
        System.out.println("[SUCCESS] Clicked first appointment");

    } catch (Exception e) {
        System.out.println("[ERROR] Failed to click appointment: " + e.getMessage());
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
     * Goes back from appointment details screen by pressing the back key.
     */
    public void goBackFromAppointmentDetails() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }
        System.out.println("Returned from appointment details");
    }
}
