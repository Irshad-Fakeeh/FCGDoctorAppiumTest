import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LabPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;
    private final String pageName = "Lab";

    public LabPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    public void navigateLab() {
        System.out.println("[INFO] ===== NAVIGATING LAB =====");

        try {
            Thread.sleep(1500); // Wait for content to load

            // Click History and go back
            clickHistory();

            goBackFromLab();
            System.out.println("[INFO] ===== LAB NAVIGATION COMPLETED =====");

        } catch (Exception e) {
            System.out.println("[ERROR] Error navigating Lab: " + e.getMessage());
            e.printStackTrace();
            try {
                goBackFromLab();
            } catch (Exception backEx) {
                System.out.println("[WARNING] Could not go back: " + backEx.getMessage());
            }
        }
    }

    private void clickHistory() {
        try {
            System.out.println("[INFO] Looking for History tab/button...");
            By historyLocator = ios
                    ? AppiumBy.accessibilityId("History")
                    : By.xpath("//*[contains(@content-desc,'History') or contains(@text,'History')]");

            WebElement historyTab = wait.until(ExpectedConditions.elementToBeClickable(historyLocator));
            historyTab.click();
            System.out.println("[SUCCESS] Clicked History inside Lab");

            Thread.sleep(2000); // Wait for history view to load

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

    private void goBackFromLab() {
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
