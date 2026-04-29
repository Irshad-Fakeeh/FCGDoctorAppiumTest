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

public class Vitals {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;
    private final String pageName = "Vitals";

    public Vitals(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Navigate through Vitals page:
     * - Click History and go back
     * - Go back from Vitals
     */
    public void navigateVitals() {
        System.out.println("[INFO] ===== NAVIGATING VITALS =====");

        try {
            // Wait for content to load
            Thread.sleep(1500);

            // Click History and go back
            // clickHistory();
            Thread.sleep(1500);

            // Go back from Vitals
            goBackFromVitals();

            System.out.println("[INFO] ===== VITALS NAVIGATION COMPLETED =====");

        } catch (Exception e) {
            System.out.println("[ERROR] Error navigating Vitals: " + e.getMessage());
            e.printStackTrace();
            try {
                goBackFromVitals();
            } catch (Exception backEx) {
                System.out.println("[WARNING] Could not go back: " + backEx.getMessage());
            }
        }
    }

    private void clickHistory() {
        try {
            System.out.println("[INFO] Looking for History tab/button...");
            String beforeActivity = getCurrentActivitySafe();
            WebElement historyTab = findClickableHistoryElement();
            historyTab.click();
            System.out.println("[SUCCESS] Clicked History inside Vitals");

            Thread.sleep(2000); // Wait for history view to load

            String afterActivity = getCurrentActivitySafe();
            if (beforeActivity != null && afterActivity != null && beforeActivity.equals(afterActivity)) {
                System.out.println("[WARNING] Vitals history tap did not change the current activity.");
            }

            // Go back from history view
            System.out.println("[INFO] Going back from History view...");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("[WARNING] Could not interact with History: " + e.getMessage());
        }
    }

    private WebElement findClickableHistoryElement() {
        List<By> locators = List.of(
                findVitalsHistoryLocator(),
                findGenericHistoryLocator());

        for (By locator : locators) {
            List<WebElement> matches = driver.findElements(locator);
            for (WebElement match : matches) {
                try {
                    if (match.isDisplayed() && match.isEnabled()) {
                        wait.until(ExpectedConditions.elementToBeClickable(match));
                        return match;
                    }
                } catch (Exception ignored) {
                    // Try the next match or locator.
                }
            }
        }

        throw new RuntimeException("Could not find a clickable History element in Vitals");
    }

    private By findVitalsHistoryLocator() {
        return ios
                ? AppiumBy.accessibilityId("Vitals History")
                : By.xpath(
                        "//*[contains(translate(@content-desc,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'vitals history')"
                                + " or contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'vitals history')]"
                                + " | //*[contains(translate(@content-desc,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'vitals')"
                                + " and contains(translate(@content-desc,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'history')]"
                                + " | //*[contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'vitals')"
                                + " and contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'history')]");
    }

    private By findGenericHistoryLocator() {
        if (ios) {
            return AppiumBy.accessibilityId("History");
        }

        return By.xpath("//*[contains(@content-desc,'History') or contains(@text,'History')]");
    }

    private String getCurrentActivitySafe() {
        try {
            if (!ios) {
                return ((AndroidDriver) driver).currentActivity();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void goBackFromVitals() {
        try {
            System.out.println("[DEBUG] Going back from " + pageName);

            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }

            System.out.println("[SUCCESS] Returned from " + pageName);

        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from " + pageName + ": " + e.getMessage());
        }
    }
}
