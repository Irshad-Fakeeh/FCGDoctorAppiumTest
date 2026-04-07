import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SchedulePage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public SchedulePage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
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
}
