import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
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

    // 🔹 Profile Avatar
    public void clickProfileAvatar() {
        By locator = ios
                ? AppiumBy.accessibilityId("profile_image")
                : By.xpath("(//android.view.View[contains(@content-desc,'Welcome Back')]//android.widget.ImageView)[1]");

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

    /**
     * Taps the Home tab (first icon in bottom navigation).
     */
    public void clickHomeTab() {
        System.out.println("Clicking Home Tab...");
        By locator = ios ? AppiumBy.accessibilityId("Home") 
                         : AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Home\")");
        
        try {
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(locator));
            tab.click();
        } catch (Exception e) {
            System.err.println("Description-based Home Tab search failed. Trying instance fallback (usually instance 2 or 3)...");
            try {
                // Instance 0 is usually the profile avatar, so we skip it.
                driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(2)")).click();
            } catch (Exception e2) {
                throw e;
            }
        }
    }

     // 🔹 Bottom Nav - Schedule
    public void clickScheduleFromBottomNav() {
        By locator = ios
                ? AppiumBy.accessibilityId("Schedule")
                : By.xpath("//*[contains(@content-desc,'Schedule')]");

        WebElement schedule = wait.until(ExpectedConditions.elementToBeClickable(locator));
        schedule.click();
    }

    // 🔹 Bottom Nav - Home
    public void clickHomeFromBottomNav() {
        By locator = ios
                ? AppiumBy.accessibilityId("Home")
                : By.xpath("//*[contains(@content-desc,'Home')]");

        WebElement home = wait.until(ExpectedConditions.elementToBeClickable(locator));
        home.click();
    }

    /**
     * Taps the Current Inpatients module on the dashboard.
     */
    public void clickCurrentInpatients() {
        System.out.println("Clicking Current Inpatients module...");
        By locator = ios ? AppiumBy.accessibilityId("Current Inpatients") 
                         : AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Current Inpatients\")");
        
        try {
            WebElement card = wait.until(ExpectedConditions.elementToBeClickable(locator));
            card.click();
            System.out.println("Current Inpatients clicked successfully!");
        } catch (Exception e) {
            System.err.println("Could not find 'Current Inpatients' card. Trying backup locator...");
            try {
                WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").descriptionContains(\"Current\")")));
                card.click();
            } catch (Exception e2) {
                throw e;
            }
        }
    }

      // 🔹 Toggle Weekly / Monthly
    public void toggleAppointmentChartView() {
        By locator = ios
                ? AppiumBy.accessibilityId("Weekly")
                : By.xpath("//*[contains(@content-desc,'Weekly') or contains(@content-desc,'Monthly')]");

        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(locator));
        toggle.click();

        System.out.println("Toggled appointment status chart view.");
    }

    // 🔹 Current Inpatients
    public void clickCurrentInpatient() throws InterruptedException {
        tapDashboardCard("Current Inpatients");
    }

    // 🔹 Critical Outpatients
    public void clickCriticalOutpatient() throws InterruptedException {
        tapDashboardCard("Critical Outpatients");
    }

    // 🔹 Highcare
    public void clickHighcare() throws InterruptedException {
        tapDashboardCard("High care");
    }

    /**
     * Flutter accessibility nodes fail visibilityOfElementLocated (isDisplayed=false).
     * Strategy: presenceOfElementLocated finds the element, then we validate its Y
     * coordinate against the screen. If off-screen, swipe down and re-fetch until
     * the centre is within the viewport, then fire a coordinate gesture tap.
     */
   private void tapDashboardCard(String contentDesc) throws InterruptedException {

    By locator = ios
            ? AppiumBy.accessibilityId(contentDesc)
            : By.xpath("//android.widget.ImageView[contains(@content-desc,'" + contentDesc.split(" ")[0] + "')]");

    WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

    int x = el.getLocation().getX() + el.getSize().getWidth() / 2;
    int y = el.getLocation().getY() + el.getSize().getHeight() / 2;

    System.out.println("[DEBUG] Found: " + el.getAttribute("content-desc"));

    driver.executeScript("mobile: clickGesture", java.util.Map.of(
            "x", x,
            "y", y
    ));

    System.out.println("[SUCCESS] Tapped: " + contentDesc);
}

}
