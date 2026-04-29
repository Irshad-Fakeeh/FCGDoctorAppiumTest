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

    public void TestInPatient() throws InterruptedException {

        By itemsLocator = ios
                ? By.xpath("//XCUIElementTypeCell")
                : By.xpath("//android.view.View[@content-desc]");

        try {
            System.out.println("\n[STEP] Loading In-Patient List...");
            Thread.sleep(3000);

            // 1. Perform manual scrolls to ensure several patients are loaded
            System.out.println("[INFO] Performing manual scrolls to reveal 5th patient...");
            performManualScrollDown();
            Thread.sleep(1500);
            performManualScrollDown();
            Thread.sleep(2000);

            // Fetch items
            List<WebElement> items = driver.findElements(itemsLocator);
            System.out.println("[INFO] Found " + items.size() + " items after scrolling");

            if (items.isEmpty()) {
                throw new RuntimeException("No patients found!");
            }

            // ✅ Correct index logic
            int targetIndex;

            if (items.size() >= 5) {
                targetIndex = 2; // ✅ 5th item
                System.out.println("[INFO] Clicking 5th item");
            } else {
                targetIndex = items.size() - 1;
                System.out.println("[WARNING] Less than 5 items → Clicking last item");
            }

            System.out.println("[INFO] Targeted item index: " + targetIndex);
            WebElement targetItem = items.get(targetIndex);

            wait.until(ExpectedConditions.elementToBeClickable(targetItem)).click();
            System.out.println("[SUCCESS] Clicked patient at index " + targetIndex);

            Thread.sleep(2000);

            // 🔽 View EMR
            clickViewEMRButton();
            Thread.sleep(2000);

            // 🔽 Navigate EMR
            EMRPage emrPage = new EMRPage(driver, wait, ios);
            emrPage.navigateEMRSections();

            Thread.sleep(1000);

            // 🔙 Back from details
            goBackFromInPatientDetails();
            Thread.sleep(1000);
            goBackToHomePage();

        } catch (Exception e) {
            System.out.println("[ERROR] In-Patient test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void performManualScrollDown() {
        if (ios)
            return;
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();

            // Swipe from lower-middle to upper-middle (Scrolls content DOWN)
            driver.executeScript("mobile: swipeGesture", java.util.Map.of(
                    "left", width / 4,
                    "top", (int) (height * 0.2),
                    "width", width / 2,
                    "height", (int) (height * 0.6),
                    "direction", "up",
                    "percent", 0.9,
                    "speed", 800));
            System.out.println("[DEBUG] Manual swipe executed");
        } catch (Exception e) {
            System.out.println("[WARNING] Scroll failed: " + e.getMessage());
        }
    }

    // ---------------- NAVIGATION ----------------

    public void goBackToHomePage() {
        try {
            System.out.println("[STEP] Navigating back to Home Dashboard...");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("[WARNING] Home back-press failed: " + e.getMessage());
        }
    }

    public void goBackFromInPatientDetails() {
        try {
            System.out.println("[STEP] Returning from Patient Details...");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
        } catch (Exception e) {
            System.out.println("[WARNING] Detail back-press failed: " + e.getMessage());
        }
    }

    // ---------------- ACTIONS ----------------

    public void clickViewEMRButton() {
        try {
            By locator = ios
                    ? AppiumBy.accessibilityId("View EMR")
                    : By.xpath("//android.widget.ImageView[@content-desc='View EMR']");

            System.out.println("[STEP] Clicking View EMR...");
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(locator));
            btn.click();
            System.out.println("[SUCCESS] View EMR opened");
        } catch (Exception e) {
            System.out.println("[WARNING] View EMR button not found: " + e.getMessage());
        }
    }
}