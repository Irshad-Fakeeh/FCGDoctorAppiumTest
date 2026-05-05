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

public class HighcarePage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public HighcarePage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    public void testHighcare() throws InterruptedException {

        By itemsLocator = ios
                ? By.xpath("//XCUIElementTypeCell")
                : By.xpath("//android.view.View[@content-desc]");

        try {
            System.out.println("\n[STEP] Loading Highcare Patient List...");
            Thread.sleep(3000);

            // Scroll once to ensure the list is fully rendered
            performManualScrollDown();
            Thread.sleep(1500);

            List<WebElement> items = driver.findElements(itemsLocator);
            System.out.println("[INFO] Found " + items.size() + " items on Highcare list");

            if (items.isEmpty()) {
                System.out.println("[WARNING] No patients found in Highcare list — returning to home");
                goBackToHomePage();
                return;
            }

            // Log first four items so we can confirm the 3rd patient
            for (int i = 0; i < Math.min(items.size(), 4); i++) {
                System.out.println("[DEBUG] items[" + i + "]: "
                        + items.get(i).getAttribute("content-desc"));
            }

            // Target 3rd patient (index 2); fall back to last item if list is smaller
            int targetIndex = (items.size() >= 3) ? 2 : items.size() - 1;
            System.out.println("[INFO] Targeting patient at index " + targetIndex + " (3rd patient)");

            WebElement targetItem = items.get(targetIndex);
            int px = targetItem.getLocation().getX() + targetItem.getSize().getWidth() / 2;
            int py = targetItem.getLocation().getY() + targetItem.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping patient[" + targetIndex + "] at (" + px + ", " + py + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", px, "y", py));
            System.out.println("[SUCCESS] Tapped patient");
            Thread.sleep(2000);

            // Open EMR
            clickViewEMRButton();
            Thread.sleep(2000);

            // Run full reusable EMR section navigation
            EMRPage emrPage = new EMRPage(driver, wait, ios);
            emrPage.navigateEMRSections();
            Thread.sleep(1000);

            // Navigate back to home
            goBackFromHighcareDetails();
            Thread.sleep(1000);
            goBackToHomePage();

        } catch (Exception e) {
            System.out.println("[ERROR] Highcare test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void performManualScrollDown() {
        if (ios) return;
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();
            driver.executeScript("mobile: swipeGesture", java.util.Map.of(
                    "left",      width / 4,
                    "top",       (int)(height * 0.2),
                    "width",     width / 2,
                    "height",    (int)(height * 0.6),
                    "direction", "up",
                    "percent",   0.9,
                    "speed",     800));
            System.out.println("[DEBUG] Scroll executed");
        } catch (Exception e) {
            System.out.println("[WARNING] Scroll failed: " + e.getMessage());
        }
    }

    private void clickViewEMRButton() {
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

    private void goBackFromHighcareDetails() {
        try {
            System.out.println("[STEP] Returning from Highcare Patient Details...");
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
        } catch (Exception e) {
            System.out.println("[WARNING] Detail back-press failed: " + e.getMessage());
        }
    }

    private void goBackToHomePage() {
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
}
