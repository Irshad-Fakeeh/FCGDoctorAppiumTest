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

public class CurrentMedicationPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;
    private final String pageName = "Current Medication";

    public CurrentMedicationPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    public void navigateCurrentMedication() {
        System.out.println("[INFO] ===== NAVIGATING CURRENT MEDICATION =====");

        try {
            Thread.sleep(2000);

            clickTab("Current");
            Thread.sleep(1500);

            clickTab("Past");
            Thread.sleep(1500);

            goBackFromCurrentMedication();
            System.out.println("[INFO] ===== CURRENT MEDICATION NAVIGATION COMPLETED =====");

        } catch (Exception e) {
            System.out.println("[ERROR] Error navigating Current Medication: " + e.getMessage());
            e.printStackTrace();
            try {
                goBackFromCurrentMedication();
            } catch (Exception backEx) {
                System.out.println("[WARNING] Could not go back: " + backEx.getMessage());
            }
        }
    }

    private void clickTab(String tabName) {
        try {
            System.out.println("[STEP] Clicking tab: " + tabName);

            By locator = ios
                    ? AppiumBy.accessibilityId(tabName)
                    : By.xpath("//*[contains(@content-desc,'" + tabName + "')]");

            List<WebElement> found = driver.findElements(locator);
            if (found.isEmpty()) {
                System.out.println("[INFO] Tab '" + tabName + "' not found — skipping");
                return;
            }

            WebElement tab = found.get(0);
            int x = tab.getLocation().getX() + tab.getSize().getWidth() / 2;
            int y = tab.getLocation().getY() + tab.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping tab '" + tabName + "' at (" + x + ", " + y + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Tapped tab: " + tabName);

        } catch (Exception e) {
            System.out.println("[WARNING] Failed to click tab '" + tabName + "': " + e.getMessage());
        }
    }

    private void goBackFromCurrentMedication() {
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
