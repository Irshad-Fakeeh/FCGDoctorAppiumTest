import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

            // Click Vitals History and go back
            clickHistory();

    
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
            System.out.println("[STEP] Clicking History from Vitals...");

            By locator = ios
                    ? AppiumBy.accessibilityId("History")
                    : By.xpath("//android.widget.ImageView[contains(@content-desc,'Temperature') or contains(@content-desc,'History')]");

            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            System.out.println("[DEBUG] Found card: " + element.getAttribute("content-desc"));

            // The card content-desc has 6 semantic lines with History at the bottom.
            // Tap at center-x, 92% of height to land on the History button row.
            int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = element.getLocation().getY() + (int)(element.getSize().getHeight() * 0.92);
            System.out.println("[DEBUG] Tapping at (" + x + ", " + y + ")");

            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Clicked History");

            Thread.sleep(2000);

            System.out.println("[INFO] Going back from History view...");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("[ERROR] History click failed: " + e.getMessage());
            e.printStackTrace();
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
