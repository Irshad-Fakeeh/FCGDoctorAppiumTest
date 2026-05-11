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
        System.out.println("[INFO] ===== SCHEDULE PAGE TEST =====");

        // Switch Virtual → Physical
        System.out.println("[STEP] Switching to Virtual tab...");
        clickTab("Virtual");
        Thread.sleep(2000);

        System.out.println("[STEP] Switching to Physical tab...");
        clickTab("Physical");
        Thread.sleep(2000);

        // Click first Physical appointment if list has data
        System.out.println("[STEP] Looking for Physical appointments...");
        clickFirstPhysicalAppointment();

        System.out.println("[INFO] ===== SCHEDULE PAGE TEST COMPLETED =====");
    }

    private void clickTab(String tabName) {
        try {
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
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Tapped tab: " + tabName);
        } catch (Exception e) {
            System.out.println("[WARNING] Could not tap tab '" + tabName + "': " + e.getMessage());
        }
    }

private void clickFirstPhysicalAppointment() throws InterruptedException {
    try {

        Thread.sleep(2000);

        // ✅ Target only appointment cards
        By locator = ios
                ? By.xpath("//XCUIElementTypeCell")
                : By.xpath("//android.view.View[contains(@content-desc,'Physical Appointment')]");

        List<WebElement> items = driver.findElements(locator);

        System.out.println("[INFO] Physical appointments found: " + items.size());

        if (items.isEmpty()) {
            System.out.println("[WARNING] No Physical Appointment found");
            return;
        }

        // ✅ First appointment
        WebElement first = items.get(0);

        System.out.println("[DEBUG] Appointment: "
                + first.getAttribute("content-desc"));

        // 🔥 Flutter-safe tap
        int x = first.getLocation().getX() + first.getSize().getWidth() / 2;
        int y = first.getLocation().getY() + first.getSize().getHeight() / 2;

        System.out.println("[DEBUG] Clicking at: " + x + "," + y);

        driver.executeScript("mobile: clickGesture", java.util.Map.of(
                "x", x,
                "y", y
        ));

        System.out.println("[SUCCESS] Clicked first Physical Appointment");

        Thread.sleep(3000);

        // 🔙 Back
        goBackFromAppointmentDetails();

        Thread.sleep(1500);

    } catch (Exception e) {
        System.out.println("[ERROR] Physical appointment click failed: " + e.getMessage());
        e.printStackTrace();
    }
}

    public void goBackFromAppointmentDetails() {
        try {
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            System.out.println("[SUCCESS] Returned from appointment details");
        } catch (Exception e) {
            System.out.println("[WARNING] Back press failed: " + e.getMessage());
        }
    }
}
