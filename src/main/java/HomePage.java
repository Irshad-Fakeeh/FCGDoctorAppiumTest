import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public HomePage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

 /**
     * Taps the doctor profile avatar in the AppBar to navigate to Profile & Settings screen.
     * Flutter renders the GestureDetector wrapping the profile image at the top-left of the AppBar.
     */
    public void clickProfileAvatar() {
        By locator = AppiumBy.accessibilityId("profile_avatar");

        try {
            WebElement avatar = wait.until(ExpectedConditions.elementToBeClickable(locator));
            avatar.click();
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP ON HOME PAGE FAIL ==============");
            System.err.println("Driver could not find the 'profile_avatar' element! Printing layout to see what screen we are actually on:");
            System.err.println("Printing getPageSource() is disabled to prevent uiAutomator crashes.");
            System.err.println("=============================================================\n");
            throw e;
        }
    }

    /**
     * Taps the Discharge tab.
     */
    public void clickDischargeTab() {
        System.out.println("Navigating to Home and waiting for UI to stabilize (5s)...");
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {} // EXTENDED BUFFER FOR STABILITY
        
        // Primary: Find by description. 
        // Fallback: Click the 3rd icon on the bottom nav if description fails.
        By descriptionLocator = ios ? AppiumBy.accessibilityId("Discharge") 
                                     : AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Discharge\")");
        
        By instanceLocator = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(3)");
        
        try {
            WebElement tab;
            try {
                tab = wait.until(ExpectedConditions.elementToBeClickable(descriptionLocator));
            } catch (Exception e) {
                System.out.println("Description-based search failed or timed out. Trying instance-based fallback...");
                tab = wait.until(ExpectedConditions.elementToBeClickable(instanceLocator));
            }
            tab.click();
            System.out.println("Discharge tab clicked successfully!");
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP ON DISCHARGE TAB CLICK FAIL ==============");
            System.err.println("Driver could not find the Discharge tab! (Both description and instance failed)");
            System.err.println("Printing getPageSource() is disabled to prevent uiAutomator crashes.");
            System.err.println("======================================================================\n");
            throw e;
        }
    }

    /**
     * Checks if the notifications screen is visible.
     */
    public boolean isNotificationsScreenVisible() {
        By locator = AppiumBy.accessibilityId("Notifications");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
